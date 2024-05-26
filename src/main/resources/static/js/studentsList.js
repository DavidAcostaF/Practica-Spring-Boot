

function deleteStudent(evt) {
    state = confirm("Are you sure you want to delete this student?");
    if (!state){
        evt.preventDefault();
    }
}