'use strict';


function rowClicked(guestId, officialId) {
    location.href = "/guest/" + guestId + "/official/" + officialId + "/schedule";
}

function openSubscribeEventModal(guestId, officialId, event) {
    $.ajax({
        url: "/guest/" + guestId + "/official/" + officialId + "/event/" + event + "/subscribe",
        success: function (data) {
            $("#subscribeEventModalHolder").html(data);
            $("#subscribeEventModal").modal("show");
        }
    });
}

function openUnsubscribeModal(guestId, officialId, event) {
    $.ajax({
        url: "/guest/" + guestId + "/official/" + officialId + "/event/" + event + "/unsubscribe",
        success: function (data) {
            $("#unsubscribeModalHolder").html(data);
            $("#unsubscribeModal").modal("show");
        }
    });
}
