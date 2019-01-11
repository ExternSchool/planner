'use strict';

function openDeleteTeacherModal(id, eid) {
    $.ajax({
        url: "/teacher/" + id + "/delete-modal",
        success: function (data) {
            $("#deleteTeacherModalHolder").html(data);
            $("#deleteTeacherModal").modal("show");
        }
    });
}
