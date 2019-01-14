'use strict';

function openCreateAccountModal() {
    $.ajax({
        url: "/guest/create",
        success: function (data) {
            $("#createAccountModalHolder").html(data);
            $("#createAccountModal").modal("show");
        }
    });
}

function openDeleteGuestModal(id) {
    $.ajax({
        url: "/guest/" + id + "/delete-modal",
        success: function (data) {
            $("#deleteGuestModalHolder").html(data);
            $("#deleteGuestModal").modal("show");
        }
    });
}

