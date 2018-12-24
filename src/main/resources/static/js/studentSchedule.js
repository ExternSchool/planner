'use strict';


function rowClicked(studentId, teacherId) {
    location.href = "/student/" + studentId + "/teacher/" + teacherId + "/schedule";
}

function openSubscribeEventModal(studentId, teacherId, event) {
    $.ajax({
        url: "/student/" + studentId + "/teacher/" + teacherId + "/event/" + event + "/subscribe",
        success: function (data) {
            $("#subscribeEventModalHolder").html(data);
            $("#subscribeEventModal").modal("show");
        }
    });
}

function openUnsubscribeModal(studentId, teacherId, event) {
    $.ajax({
        url: "/student/" + studentId + "/teacher/" + teacherId + "/event/" + event + "/unsubscribe",
        success: function (data) {
            $("#unsubscribeModalHolder").html(data);
            $("#unsubscribeModal").modal("show");
        }
    });
}
