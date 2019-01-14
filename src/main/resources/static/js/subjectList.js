'use strict';

function openDeleteSchoolSubjectModal(id) {
    $.ajax({
        url: "/subject/" + id + "/delete-modal",
        success: function (data) {
            $("#deleteSchoolSubjectHolder").html(data);
            $("#deleteSchoolSubjectModal").modal("show");
        }
    });
}

$(document).ready(function() {
    $("form").bind("keypress", function(e) {
        if (e.keyCode === 13) {
            return false;
        }
    });
});
