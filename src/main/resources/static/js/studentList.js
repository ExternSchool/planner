'use strict';

function openDeleteStudentModal(id) {
    $.ajax({
        url: "/student/" + id + "/delete-modal",
        success: function (data) {
            $("#deleteStudentModalHolder").html(data);
            $("#deleteStudentModal").modal("show");
        }
    });
}
