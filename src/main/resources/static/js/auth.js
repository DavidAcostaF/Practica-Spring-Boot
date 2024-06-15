async function login(){
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "email": email,
        "password": password
    });

    const requestOptions = {
        method: "POST",
        headers: myHeaders,
        body: raw,
        redirect: "follow"
    };

    try {

        const response = await fetch("/auth/login", requestOptions);
        const result = await response.json();
        console.log(result.jwt);
        if(response.ok){
            window.location.replace("/list");
        }

        console.log(response)
    } catch (error) {
        console.error(error);
    };
}


