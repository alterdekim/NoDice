$.fn.pressEnter = function(fn) {

    return this.each(function() {
        $(this).bind('enterPress', fn);
        $(this).keyup(function(e){
            if(e.keyCode == 13)
            {
              $(this).trigger("enterPress");
            }
        })
    });
 };

 var last_chat_id = 0;

function findUser(users, id) {
    for( let i = 0; i < users.length; i++ ) {
        if( users[i].id == id ) {
            return users[i];
        }
    }
}

function parseTime(unix_timestamp) {
    var date = new Date(unix_timestamp * 1000);
    var hours = date.getHours();
    var minutes = "0" + date.getMinutes();
    return hours + ':' + minutes.substr(-2);
}

function replyButtonClicked(obj) {
    let userid = $(obj).attr('data-userid');
    let username = $(obj).attr('data-username');
    $('#chat-message-input').val($('#chat-message-input').val() + " @" + username + "");
}

setInterval(function() {
    $.ajax({
      url: "/api/v1/notify/get/" + last_chat_id + "/",
      method: "GET"
    }).done(function(data) {
        console.log(data);
        let onlineCount = data.onlineCount;
        $(".chat-title").find("span").html(onlineCount + " online");
        let messages = data.messages;
        let users = data.users;
        if( messages.length > 0 ) {
            last_chat_id = messages[0].id;
        }
        for( let i = 0; i < messages.length; i++ ) {
            let obj = messages[i];
            let time = parseTime(obj.createdAt);
            let username = findUser(users, obj.userId).username;
            let userid = obj.userId;
            let msgtext = obj.message;
            let html = '<div class="chat-history-one"><div class="chat-history-info"><span class="time">'+time+'</span><ion-icon name="arrow-undo-outline" class="reply-button" data-userid="'+userid+'" data-username="'+username+'" onClick="replyButtonClicked(this)"></ion-icon></div><span><span class="chat-history-text chat-history-content-message"><span class="formatter"><a href="/profile/'+userid+'" class="chat-history-user"><span class="_nick">'+username+'</span></a><ion-icon name="remove-outline"></ion-icon><span>'+msgtext+'</span></span></span></span></div>';
            $(".chat-history").append(html);
        }
        $("#chat_list").find(".chat-history").css("display", "");
        $("#chat_list").find(".message-input").css("display", "");
        $("#chat_list").find(".block-content").css("display", "none");
    });
}, 5000);

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

    $.ajax({
      url: "/api/v1/games/list",
      method: "GET"
    }).done(function(data) {
        console.log(data);
    });

    // block-content

    $.ajax({
      url: "/api/v1/chat/history/100/",
      method: "GET"
    }).done(function(data) {
        console.log(data);
        let messages = data.messages;
        let users = data.users;
        if( messages.length > 0 ) {
            last_chat_id = messages[0].id;
        }
        for( let i = messages.length-1; i >= 0; i-- ) {
            let obj = messages[i];
            let time = parseTime(obj.createdAt);
            let username = findUser(users, obj.userId).username;
            let userid = obj.userId;
            let msgtext = obj.message;
            let html = '<div class="chat-history-one"><div class="chat-history-info"><span class="time">'+time+'</span><ion-icon name="arrow-undo-outline" class="reply-button" data-userid="'+userid+'" data-username="'+username+'" onClick="replyButtonClicked(this)"></ion-icon></div><span><span class="chat-history-text chat-history-content-message"><span class="formatter"><a href="/profile/'+userid+'" class="chat-history-user"><span class="_nick">'+username+'</span></a><ion-icon name="remove-outline"></ion-icon><span>'+msgtext+'</span></span></span></span></div>';
            $(".chat-history").append(html);
        }
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

    /*$.ajax({
      url: "/api/v1/games/list",
      method: "GET"
    }).done(function(data) {
        console.log(data);
    });*/
});