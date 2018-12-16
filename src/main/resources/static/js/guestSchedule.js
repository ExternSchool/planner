'use strict';


function rowClicked(guestId, officerId) {
    location.href = "/guest/" + guestId + "/officer/" + officerId + "/schedule";
}

function openReserveEventModal(guestId, officerId, event) {
    $.ajax({
        url: "/guest/" + guestId + "/officer/" + officerId + "/event/" + event + "/reserve",
        success: function (data) {
            $("#reserveEventModalHolder").html(data);
            $("#reserveEventModal").modal("show");
        }
    });
}

function openCancelReservationModal(guestId, officerId, event) {
    $.ajax({
        url: "/guest/" + guestId + "/officer/" + officerId + "/event/" + event + "/cancel",
        success: function (data) {
            $("#cancelReservationModalHolder").html(data);
            $("#cancelReservationModal").modal("show");
        }
    });
}
