var isPollingActive = true;

var last_chat_id = 0;
var rids = [];
var fids = [];

function createRoom() {
    let isprivate = $("#flexSwitchCheckDefault").is(':checked');
    let playerscnt = $("#players-count-range").val();
    $.ajax({
        url: "/api/v1/rooms/create/",
          data: {
            is_private: isprivate,
            players_count: playerscnt
          },
          method: "POST"
    }).done(function(data) {
        $("#game-creation-menu-modal").modal('hide');
    });
}

function sendInviteMessage(uid) {
    $.ajax({
        url: "/api/v1/rooms/invite/",
        method: "POST",
        data: {
            friend_id: uid
        },
        statusCode: {
            400: function() {
                $(".toast-container").append('<div class="toast" role="alert" aria-live="assertive" aria-atomic="true"><div class="toast-header"><img src="/static/images/favicon.ico" width="16" height="16" class="rounded me-2" alt="Nosedive"><strong class="me-auto">Nosedive</strong><small class="text-body-secondary">just now</small><button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button></div><div class="toast-body">Can\'t send invite.</div></div>');
                bootstrap.Toast.getOrCreateInstance($(".toast").last()).show();
            },
            200: function() {
                $(".toast-container").append('<div class="toast" role="alert" aria-live="assertive" aria-atomic="true"><div class="toast-header"><img src="/static/images/favicon.ico" width="16" height="16" class="rounded me-2" alt="Nosedive"><strong class="me-auto">Nosedive</strong><small class="text-body-secondary">just now</small><button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button></div><div class="toast-body">Successfully sent invite.</div></div>');
                bootstrap.Toast.getOrCreateInstance($(".toast").last()).show();
            }
        }
    });
}

function joinRoom(obj) {
    let roomId = $(obj).attr("data-room-id");
    $.ajax({
        url: "/api/v1/rooms/join/",
        data: {
            room_id: roomId
          },
          method: "POST"
    });
}

function takeInviteMessage(obj) {
    let roomId = $(obj).attr("data-roomId");
    $.ajax({
        url: "/api/v1/rooms/join/",
        data: {
            room_id: roomId
          },
          method: "POST"
    });
}

function takeRedirect(reds) {
    for( let i = 0; i < reds.length; i++ ) {
        window.location.assign("/game?id=" + reds[i].roomId);
    }
}

function successPolling(data) {
    console.log(data);
    data = data.result;

    let onlineCount = 0;
    let messages = [];
    let friends = [];
    let rooms = [];
    let invites = [];

    for(let i = 0; i < data.length; i++ ) {
        let res = data[i];
        switch(res.type) {
            case "OnlineUsers":
                onlineCount = res.array[0];
                break;
            case "ChatResult":
                messages = res.array;
                break;
            case "FriendResult":
                friends = res.array;
                break;
            case "RoomResult":
                rooms = res.array;
                break;
            case "InviteResult":
                invites = res.array;
                break;
            case "Redirect":
                takeRedirect(res.array);
                break;
        }
    }

    $(".chat-title").find("span").html(onlineCount + " online");
    if( messages.length > 0 ) {
        last_chat_id = messages[0]["message"]["id"];
    }
    for( let i = 0; i < messages.length; i++ ) {
        let obj = messages[i].message;
        let time = parseTime(obj.createdAt);
        let username = messages[i].user.username;
        let userid = obj.userId;
        let msgtext = obj.message;
        let html = '<div class="chat-history-one"><div class="chat-history-info"><span class="time">'+time+'</span><ion-icon name="arrow-undo-outline" class="reply-button" data-userid="'+userid+'" data-username="'+username+'" onClick="replyButtonClicked(this)"></ion-icon></div><span><span class="chat-history-text chat-history-content-message"><span class="formatter"><a href="/profile/'+userid+'" class="chat-history-user"><span class="_nick">'+username+'</span></a><ion-icon name="remove-outline"></ion-icon><span>'+msgtext+'</span></span></span></span></div>';
        $(".chat-history").append(html);
    }
    for( let i = 0; i < rooms.length; i++ ) {
        let room = rooms[i];
        if( room.action == 'ADD_CHANGE' ) {
            for( let u = 0; u < rids.length; u++ ) {
                if( rids[u].id == room.id ) {
                    rids.splice(u, 1);
                    break;
                }
            }
            rids.push({id: room.id, playerCount: room.playerCount, players: room.players});
            let room_p_html = '';
            for( let u = 0; u < room.players.length; u++ ) {
                let room_player = room.players[u];
                room_p_html += '<div class="games-room-one-body-members-one"><div class="games-room-one-body-members-one-avatar" data-avatar-id="'+room_player.avatarId+'"><a href="/profile/'+room_player.id+'"></a><div class="_online"></div></div><div class="games-room-one-body-members-one-nick"><a href="/profile/'+room_player.id+'">'+room_player.username+'</a></div></div>';
            }
            for( let u = 0; u < (room.playerCount - room.players.length); u++ ) {
                room_p_html += '<div data-room-id="'+room.id+'" onclick="joinRoom(this)" class="games-room-one-body-members-one _slot_join"><div class="games-room-one-body-members-one-avatar"><ion-icon name="add-outline" style="color: #656d78;"></ion-icon></div><div class="games-room-one-body-members-one-nick"><span>Join</span></div></div>';
            }
            let room_html = '<div class="games-room-one" data-room-id="'+room.id+'"><div class="games-room-one-body"><div class="games-room-one-body-head"><div class="games-room-one-body-head-info"><div class="_type"><div>Game</div></div></div><div class="games-room-one-body-head-actions"></div></div><div class="games-room-one-body-members">'+room_p_html+'</div></div></div>';
            let has_element = false;
            $(".games-room-one").each(function() {
                if( $(this).attr("data-room-id") == room.id ) {
                    $(this).replaceWith(room_html);
                    has_element = true;
                }
            });
            if( !has_element ) {
                $(".rooms-list").append(room_html);
            }
        } else if( room.action == 'REMOVE' ) {
            for( let u = 0; u < rids.length; u++ ) {
                if( rids[u].id == room.id ) {
                    rids.splice(u, 1);
                    break;
                }
            }
            $(".games-room-one").each(function() {
                if( $(this).attr("data-room-id") == room.id ) {
                    $(this).remove();
                }
            });
        }
    }
    $(".games-room-one-body-members-one-avatar").each(function() {
        if($(this).attr("data-avatar-id") != undefined) {
            $(this).css("background-image", "url('/image/store/"+$(this).attr("data-avatar-id")+"')");
        }
    });
    for( let i = 0; i < friends.length; i++ ) {
        let friend = friends[i];
        if( friend.action == 'ADD' ) {
            for( let u = 0; u < fids.length; u++ ) {
                if( fids[u].id == friend.id ) {
                    fids.splice(u, 1);
                    break;
                }
            }
            fids.push({id: friend.id, username: friend.username});
            let fr_html = '<div class="friend-one" data-friend-id="'+friend.id+'"><a href="/profile/'+friend.id+'" class="navbar-btn"><img class="navbar-profile-img" src="/image/store/'+friend.avatarId+'"></a><span>'+friend.username+'</span><ion-icon onClick="sendInviteMessage('+friend.id+')" name="person-add" role="img" class="md hydrated"></ion-icon></div>';
            $(".friends-online-list").append(fr_html);
        } else if( friend.action == 'REMOVE' ) {
            for( let u = 0; u < fids.length; u++ ) {
                if( fids[u].id == friend.id ) {
                    fids.splice(u, 1);
                    break;
                }
            }
            $(".friend-one").each(function() {
                if( $(this).attr("data-friend-id") == friend.id ) {
                    $(this).remove();
                }
            });
        }
    }

    for( let i = 0; i < invites.length; i++ ) {
        let _i = invites[i];
        $(".toast-container").append('<div class="toast" role="alert" aria-live="assertive" aria-atomic="true"><div class="toast-header"><img src="/static/images/favicon.ico" width="16" height="16" class="rounded me-2" alt="Nosedive"><strong class="me-auto">Nosedive</strong><small class="text-body-secondary">just now</small><button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button></div><div class="toast-body">'+_i.username+' invites you into Nosedive room.<div class="mt-2 pt-2 border-top"><button type="button" class="btn btn-primary btn-sm" data-roomId="'+_i.roomId+'" onclick="takeInviteMessage(this)">Take invite</button><button style="margin-left: 5px;" type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="toast">Close</button></div></div></div>');
        bootstrap.Toast.getOrCreateInstance($(".toast").last()).show();
    }

    if( friends.length > 0 ) {
        $(".friends-online-list").css("display", "");
        $("#friends_list").find(".block-content").css("display", "none");
    }

    if( messages.length > 0 ) {
        var clh = $('#chat_list').find(".chat-history");
        clh.scrollTop(clh.prop("scrollHeight"));
        $("#chat_list").find(".chat-history").css("display", "");
    }

    $("#chat_list").find(".message-input").css("display", "");
    $("#chat_list").find(".block-content").css("display", "none");
}

function pollServer() {
    let accessToken = $("api-tag").attr("data-access-token");
    let uid = $("api-tag").attr("data-uid");
    let poll_token = $("api-tag").attr("data-poll-token");
    if (isPollingActive) {
        window.setTimeout(function () {
            $.ajax({
                url: "/async/notify/get/",
                  data: {
                    last_chat_id: last_chat_id,
                    accessToken: accessToken,
                    poll_token: poll_token,
                    rids: JSON.stringify(rids),
                    fids: JSON.stringify(fids),
                    uid: uid
                  },
                  method: "POST"
                }).done(function(data) {
                    successPolling(data);
                    //SUCCESS LOGIC
                    pollServer();
                });
        }, 1500);
    }
}

$.fn.pressEnter = function(fn) {
    return this.each(function() {
        $(this).bind('enterPress', fn);
        $(this).keyup(function(e) {
            if(e.keyCode == 13) {
              $(this).trigger("enterPress");
            }
        })
    });
 };

function parseTime(unix_timestamp) {
    var date = new Date(unix_timestamp * 1000);
    var hours = "0" + date.getHours();
    var minutes = "0" + date.getMinutes();
    return hours.substr(-2) + ':' + minutes.substr(-2);
}

function replyButtonClicked(obj) {
    let userid = $(obj).attr('data-userid');
    let username = $(obj).attr('data-username');
    $('#chat-message-input').val($('#chat-message-input').val() + " @" + username + "");
}

$(document).ready(function() {
    $("#players-count-range").on("input", function() {
        $("label[for='players-count-range']").find("span").text($(this).val());
    });

    $('#chat-message-input').pressEnter(function() {
        let txt = $(this).val();
        $(this).val("");
        if( txt != "" ) {
            $.ajax({
                url: "/api/v1/chat/send",
                method: "POST",
                data: txt
            });
        }
    });

    // block-content

    $.ajax({
      url: "/api/v1/chat/history/100/",
      method: "GET"
    }).done(function(data) {
        console.log(data);
        let messages = data;
        if( messages.length > 0 ) {
            last_chat_id = messages[0].message.id;
        }
        for( let i = messages.length-1; i >= 0; i-- ) {
            let obj = messages[i].message;
            let time = parseTime(obj.createdAt);
            let username = messages[i].user.username;
            let userid = obj.userId;
            let msgtext = obj.message;
            let html = '<div class="chat-history-one"><div class="chat-history-info"><span class="time">'+time+'</span><ion-icon name="arrow-undo-outline" class="reply-button" data-userid="'+userid+'" data-username="'+username+'" onClick="replyButtonClicked(this)"></ion-icon></div><span><span class="chat-history-text chat-history-content-message"><span class="formatter"><a href="/profile/'+userid+'" class="chat-history-user"><span class="_nick">'+username+'</span></a><ion-icon name="remove-outline"></ion-icon><span>'+msgtext+'</span></span></span></span></div>';
            $(".chat-history").append(html);
        }
        var clh = $('#chat_list').find(".chat-history");
        clh.scrollTop(clh.prop("scrollHeight"));
        $("#chat_list").find(".chat-history").css("display", "");
        $("#chat_list").find(".message-input").css("display", "");
        $("#chat_list").find(".block-content").css("display", "none");
    });

    $.ajax({
      url: "/api/v1/market/banners/en/",
      method: "GET"
    }).done(function(data) {
        console.log(data);
        for( var i = 0; i < data.length; i++ ) {
            let obj = data[i];
            let tag = '';
            if( i != 0 ) {
                let html = '<button type="button" data-bs-target="#market-carousel" data-bs-slide-to="'+i+'" aria-label="Slide '+(i+1)+'"></button>';
                $("#market-carousel").find(".carousel-indicators").append(html);
            } else {
                tag = 'active';
            }
            let _ihtml = '<div class="carousel-item '+tag+'"><div class="carousel-item-background w-100 rounded" style="background: '+obj.gradientInfo+'; min-height: 160px"><div class="carousel-item-body"><div class="carousel-title-text">'+obj.title+'</div><div class="carousel-body-text">'+obj.description+'</div></div></div></div>';
            $("#market-carousel").find(".carousel-inner").append(_ihtml);
        }
        $(".market").find(".spinner-grow").css("display", "none");
        $(".market").find("#market-carousel").css("display", "");
    });

    pollServer();

    $(".rooms-list").css("display", "");
    $("#waiting_list").find(".block-content").css("display", "none");

    var clh = $('#chat_list').find(".chat-history");
    clh.scrollTop(clh.prop("scrollHeight"));
    $("#chat_list").find(".chat-history").css("display", "");
});