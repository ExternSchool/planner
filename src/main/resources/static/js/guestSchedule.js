'use strict';


function rowClicked(id) {
    location.href = "/guest/officer/" + id + "/schedule";
}

function openReserveEventModal(id, event) {
    $.ajax({
        url: "guest/officer/" + id + "/event/" + event + "/reserve",
        success: function (data) {
            $("#reserveEventModalHolder").html(data);
            $("#reserveEventModal").modal("show");
        }
    });
}

function openCancelReservationModal(id, event) {
    $.ajax({
        url: "guest/officer/" + id + "/event/" + event + "/cancel",
        success: function (data) {
            $("#cancelReservationModalHolder").html(data);
            $("#cancelReservationModal").modal("show");
        }
    });
}
