function triggerFile(obj) {
    $("#avatarFile").trigger('click');
}

function saveProfileInfo() {
    let display_name = $("#displayname-input").val();
    let nickname = $("#nickname-input").val();
    let pronouns = $('#pronouns-select').find(":selected").text();
    $.ajax({
        url: "/api/v1/settings/profile/save",
        data: {
            display_name: display_name,
            nickname: nickname,
            pronouns: pronouns
        },
        method: "POST"
    }).done(function() {
        window.location.reload();
    });
}

$(document).ready(function() {
    $('#avatarFile').change(function(evt) {
        var data = new FormData();
        $.each($(this)[0].files, function(i, file) {
            data.append('file-'+i, file);
        });

        $.ajax({
            url: "/settings",
            method: "POST",
            cache: false,
            contentType: false,
            processData: false,
            data: data
        }).done(function() {
            window.location.reload();
        });
    });
});