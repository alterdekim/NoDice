function followUser(obj) {
    let uid = $(obj).attr('data-profile-id');
    $.ajax({
        method: "POST",
        url: "/api/v1/friends/follow",
        data: {
            userId: uid
        }
    }).done(function() {
        window.location.reload();
    });
}

function unfollowUser(obj) {
    let uid = $(obj).attr('data-profile-id');
    $.ajax({
        method: "POST",
        url: "/api/v1/friends/remove/",
        data: {
            friend_id: uid
        }
    }).done(function() {
        window.location.reload();
    });
}