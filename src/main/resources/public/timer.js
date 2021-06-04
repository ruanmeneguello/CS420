    let clicks = 0;
    let stopwatch;
    let runningstate = 0; // 1 means the timecounter is running 0 means counter stopped
    let stoptime = 0;
    let currenttime;
    let usertoken=localStorage.getItem("token");
    let stepsTaken = [];
    let starttime;
    let previousStepTime;
    let customer = JSON.parse(localStorage.getItem("customer"));
    let startandstopbutton;
    let counterbutton;


    $(document).ready(function(){
        if (customer==null){
            window.location.href="/";//they cleared their cache or went to the timer link without logging in, but we need them to log in first
        }
        $('#dob').html(customer.birthDay);
        startandstopbutton = document.getElementById('startandstopbutton');
        counterbutton = document.getElementById('counterbutton');

        let logoutLink = document.getElementById("logout");
        logoutLink.onClick = () => {localStorage.removeItem("token"); localStorage.removeItem("customer")};
    });

    document.onkeyup = (e) => {
        if (e.which == 89) {
            onStep();
        }
    };



    let saveRapidStepTest = (rapidStepTest) => {
        $.ajax({
            type: 'POST',
            url: '/rapidsteptest',
            data: JSON.stringify(rapidStepTest), // or JSON.stringify ({name: 'jonas'}),
            statusCode:{
                401: () => window.location.href="/",
            },
            headers: { "suresteps.session.token": localStorage.getItem("token")},
            contentType: "application/text",
            dataType: 'text'
        });

    }

    let getRiskScore = () => {
        $.ajax({
            type: 'GET',
            url: '/riskscore/'+customer.email,
            success: function(data) {
                let customerRisk = JSON.parse(data);
                document.getElementById('score').innerHTML = customerRisk.score;
            },
            headers: { "suresteps.session.token": localStorage.getItem("token")},
            contentType: "application/text",
            dataType: 'text'
        });

    }


    let updateStepCount = (webSocketPayload) => {
        if(webSocketPayload.data=="startTimer"){
            startandstop();
        } else if (webSocketPayload.data.indexOf("stepCount")>-1 && runningstate ==1){
            onStep();
        }
    }

    function onStep() {
        let stepDate = new Date();
        let stepTime = stepDate.getTime();
        if (previousStepTime==null){
            previousStepTime=starttime;
        }
        let timeTakenForStep = stepTime-previousStepTime;
        stepsTaken.push(timeTakenForStep);
        previousStepTime = stepTime;
        clicks += 1;
        document.getElementById("clicks").innerHTML = clicks;
        if(clicks==30){
        	startandstop();
        	let testTime = stepTime-starttime;
            let rapidStepTest = {
               token: localStorage.getItem("token"),
               startTime: starttime,
               stopTime: stepTime,
               testTime: testTime,
               totalSteps: 30,
               stepPoints: stepsTaken,
               customer: customer
            };
            saveRapidStepTest(rapidStepTest);
            getRiskScore();
            clicks=0;
            previousStepTime=null;
            stepsTaken = [];
 //           webSocket.close();
        }

	 };

    let timecounter = (starttime) => {
        currentdate = new Date();
                stopwatch = document.getElementById('stopwatch');
         
        let timediff = currentdate.getTime() - starttime;
        if(runningstate == 0)
            {
            timediff = timediff + stoptime
            }
        if(runningstate == 1)
            {
            stopwatch.value = formattedtime(timediff);
            refresh = setTimeout('timecounter(' + starttime + ');',10);
            }
        else
            {
            window.clearTimeout(refresh);
            stoptime = timediff;
            }
    }
 

    function startandstop() {
      if(runningstate==0)
      {
        startdate = new Date();
        starttime = startdate.getTime();
        startandstop.value = 'Stop';
        startandstop.disabled=true;
        counterbutton.disabled=false;
        runningstate = 1;
        timecounter(starttime);
      }
      else
      {
        pageStateStopped();
      }
    }

    let pageStateStopped = () => {
        startandstop.value = 'Start';
        startandstop.disabled=false;
        counterbutton.disabled=true;
        runningstate = 0;
    }

    function resetstopwatch() {
        stoptime = 0;
        window.clearTimeout(refresh);
      
        if(runningstate == 1)
        {
            let resetdate = new Date();
            let resettime = resetdate.getTime();
            timecounter(resettime);
        }
        else
        {
            stopwatch.value = "0:0:0";
            document.getElementById("clicks").innerHTML = 0;
            document.getElementById('score').innerHTML='';
        }
    }

    let formattedtime = (unformattedtime) => {
        let decisec = Math.floor(unformattedtime/100) + '';
        let second = Math.floor(unformattedtime/1000);
        let minute = Math.floor(unformattedtime/60000);
        decisec = decisec.charAt(decisec.length - 1);
        second = second - 60 * minute + '';
        return minute + ':' + second + ':' + decisec;
    }
    


