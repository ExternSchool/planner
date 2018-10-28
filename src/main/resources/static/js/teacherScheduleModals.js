'use strict';

function openNewScheduleModal(id, day) {
    $.ajax({
       url: "/teacher/" + id + "/new-schedule/" + day,
       success: function (data) {
           $("#newScheduleModalHolder").html(data);
           $("#newScheduleModal").modal("show");
       }
    });
}

function openDeleteCurrentModal(id, day) {
    $.ajax({
        url: "/teacher/" + id + "/current-week/" + day,
        success: function (data) {
            $("#deleteCurrentModalHolder").html(data);
            $("#deleteCurrentModal").modal("show");
        }
    });
}

function openDeleteNextModal(id, day) {
    $.ajax({
        url: "/teacher/" + id + "/next-week/" + day,
        success: function (data) {
            $("#deleteNextModalHolder").html(data);
            $("#deleteNextModal").modal("show");
        }
    });
}
