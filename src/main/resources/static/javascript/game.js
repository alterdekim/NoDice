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
    }
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