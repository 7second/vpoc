import {init, update} from "./paper-force.js";
import {run, action} from "./controller.js";

let stompClient = null;
const NS_TOPIC = '/topic/tree/rb'

function connect() {
    let socket = new SockJS('/tree');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('connected: ' + frame);
        stompClient.subscribe(`${NS_TOPIC}/insert`, function (data) {
            console.log('insert: ' + data.body);
        });

        stompClient.subscribe(`${NS_TOPIC}/supervisor`, function (data) {
            console.log('supervisor: ' + data.body);
            $("#debug_alert").text(data.body);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

$(function () {
    connect();
    let paper = init();
    // $("#run").click(run.bind(null, false));
    // $("#run").click(update.bind(null, paper));
    $("#debug").click(run.bind(null, true));

    $("#go").click(action.bind(null, 'go'));
    $("#step").click(action.bind(null, 'step'));
    $("#pause").click(action.bind(null, 'pause'));
});