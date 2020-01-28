drawBoard(361);
$( document ).ready(function() {
    connect();
});

var stompClient = null;
var username = "";
var bgc = "#c29861";

function drawBoard(size) {
    for(i = 0; i<size; i++){
        $("#slots").append('<li class="dzban"id='+i+'></li>')
    }
}

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);
    stompClient.subscribe('/topic/color', gameOn);
    stompClient.send("/app/game.addUser", {},
        JSON.stringify({player: username})
    )
}

function onError(error) { alert("Something went wrong"); }

function sendMessage(slot) {
    if(slot && stompClient) {
        var gameMessage = {
            player: username,
            content: slot,
            type: 'MOVE'
        };
        stompClient.send("/app/game.sendMessage", {}, JSON.stringify(gameMessage));
    }
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    if (message.type === 'MOVE'){
        document.getElementById(message.content).style.background = message.player;
    } else if (message.type === 'ERROR' && message.player === username){
        alert(message.content);
    } else if (message.type === 'REMOVE'){
        document.getElementById(message.content).style.background = bgc;
    }
}

function gameOn(payload) {
    var message = JSON.parse(payload.body);
    if(username === ""){
        username = message.player;
    }
}

$(".dzban").click(function(){
    var siup = $(this).attr('id');
    sendMessage(siup);
});