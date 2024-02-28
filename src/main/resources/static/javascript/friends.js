$(document).ready(function() {
    $(".friends-list-one").hover(function() {
        $(this).find(".friends-list-one-info").find(".friends-list-one-info-actions").css("display", "");
    }, function() {
        $(this).find(".friends-list-one-info").find(".friends-list-one-info-actions").css("display", "none");
    });
});

function removeFriend(obj) {
    let friend_id = $(obj).attr("data-friend-id");
    $.ajax({
        url: "/api/v1/friends/remove/",
        method: "POST",
        data: {
            friend_id: friend_id
        }
    }).done(function() {
        $(obj).parent().parent().parent().remove();
    });
}

function acceptFriend(obj) {
    let friend_id = $(obj).attr("data-friend-id");
    $.ajax({
        method: "POST",
        url: "/api/v1/friends/follow",
        data: {
            userId: friend_id
        }
    }).done(function() {
        $(obj).parent().parent().parent().remove();
    });
}