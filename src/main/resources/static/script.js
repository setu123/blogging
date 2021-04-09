function passwordMatches(form) {
    if(form.password.value != form.confirmPassword.value){
        alert("Passwords don't match");
        return false;
    }else{
        return true;
    }
}