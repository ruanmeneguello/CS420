//© 2021 Sean Murdock

let userName = "";
let password = "";
let usertoken="";
let verifyPassword = "";
//let passwordRegEx=/((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{6,100})/;



    $(document).ready(function(){
        startandstopbutton = document.getElementById('startandstopbutton');
        counterbutton = document.getElementById('counterbutton');
        let hash= location.hash;//will include the #
        let hashparts = hash.split("#");
        if (hashparts.length >2 ) {
            usertoken = hashparts[1];// the url should look like https://stedi.me/timer.html#4c2286a7-8fdc-47c5-b972-739769554c88
            validateToken();//check if token is expired, if not display the email, if expired send to login
        }
        if(document.location=='/reset.html'){//we are resetting the password
            const userName=sessionStorage.getItem('userName');
            $('#email').html(userName);
        }
    });

function setusername(){
    userName = $("#username").val();
}

function setuserpassword(){
    password = $("#password").val();
    var valid=checkvalidpassword(password);
    if (!valid){
        alert('Password must contain at least 6 digits, upper, lower, number, and symbol');
    }
}

function setverifypassword(){
    verifyPassword = $("#verifypassword").val();
    if (verifyPassword!=password){
        alert('Passwords must be entered the same twice');
    }
}


function checkvalidpassword(password){

let validPassword=true;

    $.ajax({
       type: 'POST',
        url: '/complexity',
        data:JSON.stringify({password, verifyPassword}),
        error: function(xhr){validPassword=false},
        contentType: "application/text",
        dataType: 'text'
     }
    )

return validPassword;
}

function userlogin(){
    setuserpassword();
    setusername();
    $.ajax({
        type: 'POST',
        url: '/login',
        data: JSON.stringify({userName, password}),
        success: function(data) {
            window.location.href = "/timer.html#"+data;//add the token to the url
        },
        error: function(xhr){
            alert('Invalid username or password was entered.')
        },
        contentType: "application/text",
        dataType: 'text'
    });

}

function changePassword(){
    $.ajax({
        type: 'POST',
        url: '/reset',
        data: JSON.stringify({password, verifyPassword}),
        success: function(data){
            alert("Password successfully updated!");
            window.location.href="/index.html"
        },
        contentType: "application/text",
        dataType:"text"


    })

}

const requestreset = async ()=>{
    const cellNumber = document.getElementById("cellphone").value;
    console.log(`Cell Number ${cellNumber}`);

    const options = {
        method:'POST',
        "content-type":"application/text"
    }

    await fetch("https://dev.stedi.me/twofactorlogin/"+cellNumber,options);

    sessionStorage.setItem("cellNumber",cellNumber);

    document.location="/enterotp.html";
}

const enterotp = async ()=>{
    const otp = document.getElementById("otp").value;
    const cellNumber = sessionStorage.getItem("cellNumber");
    const options = {
        method:'POST',
        headers:{
            "content-type":"application/json"
        },
        body:JSON.stringify({
            phoneNumber:cellNumber,
            oneTimePassword:otp
        })
    }

    const resetTokenResponse = await fetch('https://dev.stedi.me/twofactorlogin',options)
    const resetToken = await resetTokenResponse.text();
    sessionStorage.setItem("suresteps.session.token",resetToken);

    const verificationResponse = await fetch('https://dev.stedi.me/validate/'+resetToken);
    const userName = await verificationResponse.text();

    sessionStorage.setItem('userName',userName);

    document.location='/reset.html';

}

const enterFunction = (event) =>{
    if (event.keyCode === 13){
        event.preventDefault();
        $("#loginbtn").click();
    }
}

var passwordField = document.getElementById("password");

var cellPhoneField = document.getElementById("cellphone");

passwordField!=null && passwordField.addEventListener("keyup", enterFunction);

cellPhoneField!=null && cellPhoneField.addEventListener("keyup",enterFunction);