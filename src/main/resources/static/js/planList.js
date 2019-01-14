'use strict';

function openDeleteStudyPlanModal(id) {
    $.ajax({
        url: "/plan/" + id + "/delete-modal",
        success: function (data) {
            $("#deleteStudyPlanHolder").html(data);
            $("#deleteStudyPlanModal").modal("show");
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
