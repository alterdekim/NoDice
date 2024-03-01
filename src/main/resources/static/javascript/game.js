const player_html = '<div class="player" th:data-pid="${p.id}" onClick="drop(this)"><p class="timeout"></p><p class="nickname" th:text="${p.name}"></p><p class="money" th:text="${p.money}"></p><div class="dropbox" style="display: none"></div> <!-- margin-top: -35px; --></div>';

var stompClient = null;

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendMessage(message) {
    stompClient.send("/", {}, JSON.stringify({'body': JSON.stringify(message), 'accessToken': $("api-tag").attr("data-access-token"), 'uid': $("api-tag").attr("data-uid"), 'roomId': $("api-tag").attr("data-room-id")}));
}

$(document).ready(function() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/', function (message) {
            //showMessage(JSON.parse(message.body).content);
        });
    });
});