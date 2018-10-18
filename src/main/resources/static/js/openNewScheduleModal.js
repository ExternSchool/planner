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
