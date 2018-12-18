'use strict';


function rowClicked(guestId, officerId) {
    location.href = "/guest/" + guestId + "/officer/" + officerId + "/schedule";
}

function openSubscribeEventModal(guestId, officerId, event) {
    $.ajax({
        url: "/guest/" + guestId + "/officer/" + officerId + "/event/" + event + "/subscribe",
        success: function (data) {
            $("#subscribeEventModalHolder").html(data);
            $("#subscribeEventModal").modal("show");
        }
    });
}

function openUnsubscribeModal(guestId, officerId, event) {
    $.ajax({
        url: "/guest/" + guestId + "/officer/" + officerId + "/event/" + event + "/unsubscribe",
        success: function (data) {
            $("#unsubscribeModalHolder").html(data);
            $("#unsubscribeModal").modal("show");
        }
    });
}
