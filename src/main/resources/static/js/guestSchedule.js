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

function openCancelSubscriptionModal(guestId, officerId, event) {
    $.ajax({
        url: "/guest/" + guestId + "/officer/" + officerId + "/event/" + event + "/cancel",
        success: function (data) {
            $("#cancelSubscriptionModalHolder").html(data);
            $("#cancelSubscriptionModal").modal("show");
        }
    });
}
