//button modal materialize initialization


//knows which anchor tag trigger the comment deletion
//efficiency purpose: only one modal is rendered.
let commentId;


document.addEventListener('DOMContentLoaded', function () {
    const elems = document.querySelectorAll('.modal');
    const instance = M.Modal.init(elems);

    const btn = document.getElementById("modal-delete-button");
    btn.addEventListener("click", function () {
        instance[0].open()
    });
    const location = window.location.href
    if (location.includes("?error"))
        instance[0].open();
    if (location.includes("?commentError"))
        instance[1].open();
});