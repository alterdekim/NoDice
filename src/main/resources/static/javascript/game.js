class Chip {
    constructor(obj1) {
        this.obj = obj1;
    }

    get x() {
        return this.obj.attr("data-x");
    }

    get y() {
        return this.obj.attr("data-y");
    }

    get uid() {
        return this.obj.attr("data-uid");
    }

    get color() {
        return this.obj.attr("data-color");
    }

    set x(x) {
        this.obj.attr("data-x", x);
    }

    set y(y) {
        this.obj.attr("data-y", y);
    }

    set uid(uid) {
        this.obj.attr("data-uid", uid);
    }

    set color(color) {
        this.obj.attr("data-color", color);
    }

    get dom() {
        return this.obj;
    }
}

class Board {
    constructor() {
        this.board_chips = [];
    }

    get chips() {
        return this.board_chips;
    }

    set chips(chips) {
        this.board_chips = chips;
    }
}

const top_offset = 18;

const board = new Board();

var socket = null;

function disconnect() {
    if (socket !== null) {
        socket.close();
    }
    console.log("Disconnected");
}

function sendMessage(message, type) {
    socket.send(JSON.stringify({
        'type': type,
        'body': JSON.stringify(message),
        'accessToken': $("api-tag").attr("data-access-token"),
        'uid': $("api-tag").attr("data-uid"),
        'roomId': $("api-tag").attr("data-room-id")
    }));
}

$(document).ready(function() {
     let chips = [];
     $(".chip").each(function() {
         chips.push(new Chip($(this)));
     });
     board.chips = chips;

     socket = new SockJS('/websocket');

     socket.onopen = function() {
         console.log('open');
         sendMessage({}, 'InfoRequest');
     };

     socket.onmessage = function(e) {
         console.log('message', e.data);
         showMessage(JSON.parse(e.data));
     };

     socket.onclose = function() {
         console.log('close');
         window.location.assign("/games");
     };
});

function showMessage(message) {
    console.log('GOT IT');
    switch(message.type) {
        case 'PlayersList':
            parsePlayersList(JSON.parse(message.body));
            break;
        case 'BoardGUI':
            parseBoardGUI(JSON.parse(message.body));
            break;
        case 'ChangeBoardTileState':
            changeBoardState(JSON.parse(message.body));
            break;
        case 'AssignChip':
            assignChip(JSON.parse(message.body));
            break;
        case 'ChipMove':
            chipMove(JSON.parse(message.body));
            break;
    }
}

function clamp(number, min, max) {
    return Math.max(min, Math.min(number, max));
}

function chipMove(body) {
    let nc = board.chips.find((c) => c.uid == body.uid);
    if( nc == undefined ) return;
    nc.x = body.x;
    nc.y = body.y;
    moveChip(nc);
}

const corners = [
    {x: 0, y: 0},
    {x: 10, y: 0},
    {x: 10, y: 10},
    {x: 0, y: 10}
];

const chips_margins = [[],
    [{top: 40, left: 20}],
    [{top: 25, left: 20}, {top: 50, left: 20}],
    [{top: 20, left: 20}, {top: 45, left: 20}, {top: 70, left: 20}],
    [{top: 20, left: 20}, {top: 45, left: 28}, {top: 70, left: 20}, {top: 45, left: 8}],
    [{top: 20, left: 20}, {top: 45, left: 25}, {top: 70, left: 20}, {top: 70, left: 10}, {top: 45, left: 10}]
];

function distinct(arr) {
    for(let i = 0; i < arr.length; i++) {
        let a = arr[i];
        let cnt = 0;
        for(let u = 0; u < arr.length; u++) {
            if(a.x == arr[u].x && a.y == arr[u].y) {
                cnt++;
            }
        }
        if( cnt > 1 ) {
            arr.splice(i, 1);
            i = 0;
        }
    }
    return arr;
}

function includes(pos) {
    for(let i = 0; i < corners.length; i++) {
        if( pos.x == corners[i].x && pos.y == corners[i].y ) {
            return true;
        }
    }
    return false;
}

function moveChip(chip) {
    $(chip.dom).css("background-color", chip.color);
    let offsetY = (Math.max(chip.y-1, 0) * 55) + (chip.y * 2) + (clamp(chip.y, 0, 1) * 100) + 18;
    let offsetX = (Math.max(chip.x-1, 0) * 55) + (chip.x * 2) + (clamp(chip.x, 0, 1) * 100);
    $(chip.dom).animate({marginTop: offsetY, marginLeft: offsetX}, 1000);
}

function moveChips() {
    for(let i = 0; i < board.chips.length; i++ ) {
        let chip = board.chips[i];
        if( chip.uid == 0 ) continue;
        moveChip(chip);
    }
}

function refreshChips() {
    let positions = distinct(board.chips.filter((c) => c.uid != 0).map(({ x, y }) => ({ x, y })));
    for( let i = 0; i < positions.length; i++ ) {
        let a = board.chips.filter((c) => c.x == positions[i].x && c.y == positions[i].y && c.uid != 0);
        if( includes(positions[i]) ) {
            switch(a.length) {
                case 1:
                    $(a[0].dom).animate({marginTop: "+=40", marginLeft: "+=40"}, 500);
                    break;
                case 2:
                    $(a[0].dom).animate({marginTop: "+=20", marginLeft: "+=40"}, 500);
                    $(a[1].dom).animate({marginTop: "+=60", marginLeft: "+=40"}, 500);
                    break;
                case 3:
                    $(a[0].dom).animate({marginTop: "+=40", marginLeft: "+=15"}, 500);
                    $(a[1].dom).animate({marginTop: "+=40", marginLeft: "+=40"}, 500);
                    $(a[2].dom).animate({marginTop: "+=40", marginLeft: "+=65"}, 500);
                    break;
                case 4:
                    $(a[0].dom).animate({marginTop: "+=15", marginLeft: "+=20"}, 500);
                    $(a[1].dom).animate({marginTop: "+=15", marginLeft: "+=60"}, 500);
                    $(a[2].dom).animate({marginTop: "+=55", marginLeft: "+=20"}, 500);
                    $(a[3].dom).animate({marginTop: "+=55", marginLeft: "+=60"}, 500);
                    break;
                case 5:
                    $(a[0].dom).animate({marginTop: "+=15", marginLeft: "+=20"}, 500);
                    $(a[1].dom).animate({marginTop: "+=15", marginLeft: "+=60"}, 500);
                    $(a[2].dom).animate({marginTop: "+=55", marginLeft: "+=20"}, 500);
                    $(a[3].dom).animate({marginTop: "+=55", marginLeft: "+=60"}, 500);
                    $(a[4].dom).animate({marginTop: "+=35", marginLeft: "+=40"}, 500);
                    break;
            }
        } else {
            if( positions[i].x > 0 && positions[i].x < 10 ) {
                for(let u = 0; u < a.length; u++) {
                    $(a[u].dom).animate({marginTop: "+=" + chips_margins[a.length][u].top, marginLeft: "+=" + chips_margins[a.length][u].left}, 500);
                }
            } else {
                for(let u = 0; u < a.length; u++) {
                    $(a[u].dom).animate({marginTop: "+=" + chips_margins[a.length][u].left, marginLeft: "+=" + chips_margins[a.length][u].top}, 500);
                }
            }
        }
    }
}

function assignChip(body) {
    let nc = board.chips.find((c) => c.uid == 0);
    if( nc == undefined ) return;
    nc.uid = body.uid;
    nc.color = body.color;
    nc.x = body.x;
    nc.y = body.y;
    $(nc.obj).css("display", "");
    moveChip(nc);
}

function changeBoardState(body) {
    let board_tile = $(".board_field[data-fid='" + body.uid + "']");
    $(board_tile).find(".cost").css("background-color", "#"+body.color);
    $(board_tile).find(".cost").html(body.cost);
    $(board_tile).find(".lcost").css("background-color", "#"+body.color);
    $(board_tile).find(".lcost").html(body.cost);
    $(board_tile).find("._star").html(body.stars);
    $(board_tile).find("img").attr("src", body.img);
    $(board_tile).find(".fv").css("background-color", "#"+body.ownerColor);
    $(board_tile).find(".fh").css("background-color", "#"+body.ownerColor);
}

function parseBoardGUI(body) {
    let t_html = '';
    for( let i = 0; i < body.top.length; i++ ) {
        let board_tile = body.top[i];
        t_html += '<div data-fid="'+board_tile.uid+'" class="board_field" style="grid-column: '+board_tile.id+'"><div class="cost" style="background-color: #'+board_tile.color+'">'+board_tile.cost+'</div><div class="fh" style="background-color: #'+board_tile.ownerColor+'"><div class="iconH"><img src="'+board_tile.img+'" class="vertImg"></div><div class="stars"><span class="_star">'+board_tile.stars+'</span></div></div></div>';
    }
    $("#top_board_tiles").replaceWith(t_html);

    let r_html = '';
    for( let i = 0; i < body.right.length; i++ ) {
        let board_tile = body.right[i];
        r_html += '<div data-fid="'+board_tile.uid+'" class="board_field" style="grid-column: 11"><div class="rcost" style="background-color: #'+board_tile.color+'">'+board_tile.cost+'</div><div class="fv" style="background-color: #'+board_tile.ownerColor+'"><div class="iconV"><img src="'+board_tile.img+'" class="horImg"></div><div class="stars hstars"><span class="_star _hstar">'+board_tile.stars+'</span></div></div></div>';
    }
    $("#right_board_tiles").replaceWith(r_html);

    let b_html = '';
    for( let i = 0; i < body.bottom.length; i++ ) {
        let board_tile = body.bottom[i];
        b_html += '<div data-fid="'+board_tile.uid+'" class="board_field" style="grid-row: 11; grid-column: '+board_tile.id+'"><div class="fh" style="background-color: #'+board_tile.ownerColor+'"><div class="iconH"><img class="vertImg" src="'+board_tile.img+'"></div><div class="stars"><span class="_star _vstar">'+board_tile.stars+'</span></div></div><div class="cost" style="background-color: #'+board_tile.color+'">'+board_tile.cost+'</div></div>';
    }
    $("#bottom_board_tiles").replaceWith(b_html);

    let l_html = '';
    for( let i = 0; i < body.left.length; i++ ) {
        let board_tile = body.left[i];
        l_html += '<div data-fid="'+board_tile.uid+'" class="board_field" style="grid-column: 1; grid-row: '+board_tile.id+'"><div class="lcost" style="background-color: #'+board_tile.color+'">'+board_tile.cost+'</div><div class="fv" style="background-color: #'+board_tile.ownerColor+'"><div class="iconV"><img src="'+board_tile.img+'" class="horImg"></div><div class="stars hstars _hhstar"><span class="_star _hstar">'+board_tile.stars+'</span></div></div></div>';
    }
    $("#left_board_tiles").replaceWith(l_html);

    let corners = $(".corner");
    for(let i = 0; i < body.corners.length; i++ ) {
        $(corners[i]).find("img").attr("src", body.corners[i].img);
    }
    resizeTable();
}

function parsePlayersList(body) {
    let p_html = '';
    for( let i = 0; i < body.length; i++ ) {
        let player = body[i];
        p_html += '<div class="player" data-pid="'+player.userId+'" onClick="drop(this)"><p class="timeout"></p><p class="nickname">'+player.displayName+'</p><p class="money">'+player.money+'</p><div class="dropbox" style="display: none"></div> <!-- margin-top: -35px; --></div>';
    }
    $(".players").append(p_html);
}