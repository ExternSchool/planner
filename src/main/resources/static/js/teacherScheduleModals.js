'use strict';

function openNewScheduleModal(id, day) {
    $.ajax({
       url: "/teacher/" + id + "/day/" + day + "/modal-template",
       success: function (data) {
           $("#newScheduleModalHolder").html(data);
           $("#newScheduleModal").modal("show");
       }
    });
}

function openNewCurrentModal(id, day) {
    $.ajax({
        url: "/teacher/" + id + "/new-current/" + day,
        success: function (data) {
            $("#newCurrentModalHolder").html(data);
            $("#newCurrentModal").modal("show");
        }
    });
}

function openDeleteCurrentModal(id, day) {
    $.ajax({
        url: "/teacher/" + id + "/current-week/" + day,
        success: function (data) {
            $("#cancelCurrentModalHolder").html(data);
            $("#cancelCurrentModal").modal("show");
        }
    });
}

function openNewNextModal(id, day) {
    $.ajax({
        url: "/teacher/" + id + "/new-next/" + day,
        success: function (data) {
            $("#newNextModalHolder").html(data);
            $("#newNextModal").modal("show");
        }
    });
}

function openDeleteNextModal(id, day) {
    $.ajax({
        url: "/teacher/" + id + "/next-week/" + day,
        success: function (data) {
            $("#cancelNextModalHolder").html(data);
            $("#cancelNextModal").modal("show");
        }
    });
}

function openDeleteEventModal(id, eid) {
    $.ajax({
        url: "/teacher/" + id + "/event/" + eid + "/modal",
        method: 'POST',
        success: function (data) {
            $("#deleteEventModalHolder").html(data);
            $("#deleteEventModal").modal("show");
        }
    });
}
