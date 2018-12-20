'use strict';

function openCreateAccountModal() {
    $.ajax({
        url: "/guest/create",
        success: function (data) {
            $("#createAccountModalHolder").html(data);
            $("#createAccountModal").modal("show");
        }
    });
    console.log("openCreateAccountModal");
}
