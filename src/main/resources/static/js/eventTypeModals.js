'use strict';

function openDeleteEventTypeModal(id) {
    $.ajax({
        url: "/event/type/" + id + "/modal",
        success: function (data) {
            $("#deleteEventTypeHolder").html(data);
            $("#deleteEventTypeModal").modal("show");
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
