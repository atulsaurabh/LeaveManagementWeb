function openAddCatagoryForm(url)
{
   url="/ajax/" + url;
   $('#placeholder').load(url,function (data) {
       $('#datepicker').datepicker({dateFormat:"dd/mm/yy"});
       $('#datepicker1').datepicker({dateFormat:"dd/mm/yy"});
       $('#applicationDate').datepicker({dateFormat:"dd/mm/yy"});
       $('#datepicker1').on("change",function () {

        var first = $('#datepicker').val();
        var second = $('#datepicker1').val();
        var days = daydiff(parseDate(first),parseDate(second))+1;
        if(days < 0)
        {
            alert("To Date Can Not Be Less Than From Date");
            $('#datepicker1').val('');
        }

       })
   });
}


/*function openAddCatagoryFormForLeave(url)
{
    url="/ajax/" + url;
    $('#placeholder').load(url,function (data) {
        $('#datepicker').datepicker({dateFormat:"dd/mm/yy"});
        $('#datepicker').on("change",function () {
            var seconddate = $('#datepicker1').val();
            var firstdate = $('datepicker').val();
            if(seconddate != null && seconddate != '')
            {

                var days = daydiff(parseDate(firstdate),parseDate(seconddate));
                if(days < 0)
                {
                    alert("To Date Can Not Be Less Than From Date");
                    $('#datepicker').val('');
                    $('#datepicker1').val('');
                }
                else
                {
                    loadDayCalculation(firstdate,seconddate);
                }
            }
        });
        $('#datepicker1').datepicker({dateFormat:"dd/mm/yy"});
        $('#datepicker1').on("change",function () {

            var first = $('#datepicker').val();
            var second = $('#datepicker1').val();
            if(first == null || first == '')
            {
                alert("Kindly Choose From Date");
            }
            else
            {

                var days = daydiff(parseDate(first),parseDate(second));
                if(days < 0)
                {
                    alert("To Date Can Not Be Less Than From Date");
                    $('#datepicker1').val('');
                }
                else
                {
                    loadDayCalculation(first,second);
                }
            }
        })
    });
}*/



function loadDayCalculation(fromdate,todate)
{
    var httprequest=new XMLHttpRequest();
    httprequest.open('POST','/ajax/calculatelistofdates.do',true);
    httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    httprequest.onreadystatechange =  function (){
        if(httprequest.readyState == 4 && httprequest.status == 200)
        {
            $('#leavewindow').html(httprequest.responseText);
        }
    };
    httprequest.send("fromdate="+encodeURIComponent(fromdate)+"&todate="+encodeURIComponent(todate));
}

function loadEmployeeSearchResult()
{
   var searchall="";
     if($('#include').is(':checked'))
     {
        searchall += encodeURIComponent("1");
     }
     else
     {
         searchall += encodeURIComponent("0");
     }
sendDataForEmployeeSearch(searchall,document.getElementById("employeeid").value,document.getElementById("departments").value);
}

function loadAdvancedEmployeeSearchResult()
{
    var searchall="";
    if($('#include').is(':checked'))
    {
        searchall += encodeURIComponent("1");
    }
    else
    {
        searchall += encodeURIComponent("0");
    }
    var empid = $('#ename').val();
    var deptid= $('#edept').val();
    sendDataForAdvancedEmployeeSearch(searchall,empid,deptid);
}

function sendDataForAdvancedEmployeeSearch(search,empid,deptid)
{
    if (search == 0 && (empid == null || empid == ''))
        alert("Either supply Employee name/id or select include all "+empid);
    else
    {
        var searchall="searchall="+search;
        var httprequest=new XMLHttpRequest();
        httprequest.open('POST','/ajax/advancesearchemployee.do',true);
        httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
        httprequest.onreadystatechange =  function ()
        {
            if(httprequest.readyState == 4 && httprequest.status == 200)
            {
                var data = httprequest.responseText;
                document.getElementById("search_result").innerHTML = data;
            }

        };
        var load = searchall+"&empid="+empid+"&deptid="+deptid;
        httprequest.send(load);
    }

}




function sendDataForEmployeeSearch(search,empid,deptid)
{
    var searchall="searchall="+search;
    var httprequest=new XMLHttpRequest();
    httprequest.open('POST','/ajax/searchemployee.do',true);
    httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    httprequest.onreadystatechange =  function ()
    {
        if(httprequest.readyState == 4 && httprequest.status == 200)
        {
            var data = httprequest.responseText;
            document.getElementById("search_result").innerHTML = data;
        }

    };
    var load = searchall+"&empid="+empid+"&deptid="+deptid;
    httprequest.send(load);
}


function fetchEmployeeRecord(keycode,month)
{
    var code = keycode.keyCode;
    if(code == 13)
    {
        var empid=document.getElementById("employeeid").value;
        var httprequest=new XMLHttpRequest();
        httprequest.open('POST','/ajax/fetchemployee.do',true);
        httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
        httprequest.onreadystatechange =  function ()
        {
            if(httprequest.readyState == 4 && httprequest.status == 200)
            {
                var data = httprequest.responseText;

                document.getElementById("search_result").innerHTML = data;
                var year = (new Date()).getFullYear();
                var minDate;
                var maxDate;
                if(month>=1 && month<=6)
                {
                    minDate=new Date(year,0,1);
                    maxDate=new Date(year,5,30);
                }

                else
                {
                    minDate=new Date(year,6,1);
                    maxDate=new Date(year,11,31);
                }

               $('#applicationDate').datepicker({dateFormat:"dd/mm/yy"});
                var today=new Date();
                var dd = today.getDate();
                var mm = today.getMonth()+1; //January is 0!

                var yyyy = today.getFullYear();
                if(dd<10){
                    dd='0'+dd;
                }
                if(mm<10){
                    mm='0'+mm;
                }
                var today = dd+'/'+mm+'/'+yyyy;
                $('#applicationDate').val(today);
              $('#fromDate').datepicker({
                                         dateFormat : "dd/mm/yy",
                                          minDate: minDate,
                                          maxDate: maxDate
                                        }
                                      );
                $('#toDate').datepicker({dateFormat : "dd/mm/yy",
                    minDate: minDate,
                    maxDate: maxDate
                });
                $('#toDate').on("change",function () {
                    var noofdays = daydiff(parseDate($('#fromDate').val()),parseDate($('#toDate').val()))+1;
                    if(noofdays < 0)
                    {
                        alert("To Date Can Not Be Less Than From Date");
                        $('#toDate').val('');
                    }
                    else
                    {
                        $('#noofdays').val(noofdays+1);
                        loadDayCalculation($('#fromDate').val(),$('#toDate').val());

                    }
                })
            }

        };
        var load="empid="+encodeURIComponent(empid);
        httprequest.send(load);
    }

}

function fetchEmployeeRecordById(empid,month)
{
        var httprequest=new XMLHttpRequest();
        httprequest.open('POST','/ajax/fetchemployee.do',true);
        httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
        httprequest.onreadystatechange =  function ()
        {
            if(httprequest.readyState == 4 && httprequest.status == 200)
            {
                var data = httprequest.responseText;

                document.getElementById("search_result").innerHTML = data;
                var year = (new Date()).getFullYear();
                var minDate;
                var maxDate;
                if(month>=1 && month<=6)
                {
                    minDate=new Date(year,0,1);
                    maxDate=new Date(year,5,30);
                }

                else
                {
                    minDate=new Date(year,6,1);
                    maxDate=new Date(year,11,31);
                }
                $('#applicationDate').datepicker({dateFormat:"dd/mm/yy"});
                var today=new Date();
                var dd = today.getDate();
                var mm = today.getMonth()+1; //January is 0!

                var yyyy = today.getFullYear();
                if(dd<10){
                    dd='0'+dd;
                }
                if(mm<10){
                    mm='0'+mm;
                }
                var today = dd+'/'+mm+'/'+yyyy;
                $('#applicationDate').val(today);

                $('#fromDate').datepicker({
                        dateFormat : "dd/mm/yy",
                        minDate: minDate,
                        maxDate: maxDate
                    }
                );
                $('#toDate').datepicker({dateFormat : "dd/mm/yy",
                    minDate: minDate,
                    maxDate: maxDate
                });
                $('#toDate').on("change",function () {
                    calculateNumberofDays();
                    loadDayCalculation($('#fromDate').val(),$('#toDate').val());
                })
            }

        };
        var load="empid="+encodeURIComponent(empid);
        httprequest.send(load);
}


function fetchLeaveApplication(event)
{
   var code = event.keyCode;

   if(code == 13)
   {
       var empid=document.getElementById("employeeid").value;
       var httprequest=new XMLHttpRequest();
       httprequest.open('POST','/ajax/fetchemployeeleaveapplication.do',true);
       httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
       httprequest.onreadystatechange =  function ()
       {
           if(httprequest.readyState == 4 && httprequest.status == 200)
           {
               var data = httprequest.responseText;
               document.getElementById("search_result").innerHTML = data;
               $('#fromDate').datepicker({dateFormat : "dd/mm/yy"});
               $('#toDate').datepicker({dateFormat : "dd/mm/yy"});
               $('#toDate').on("change",function () {
                   calculateNumberofDays();
               })
           }

       };
       var load="empid="+encodeURIComponent(empid);
       httprequest.send(load);
   }

}


function fetchLeaveApplicationById(empid)
{



        //var empid=document.getElementById("employeeid").value;
        var httprequest=new XMLHttpRequest();
        httprequest.open('POST','/ajax/fetchemployeeleaveapplication.do',true);
        httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
        httprequest.onreadystatechange =  function ()
        {
            if(httprequest.readyState == 4 && httprequest.status == 200)
            {
                var data = httprequest.responseText;
                document.getElementById("search_result").innerHTML = data;
            }

        };
        var load="empid="+encodeURIComponent(empid);
        httprequest.send(load);
}


function revertLeave(empid,leaveid,applycriteria)
{
    var httprequest=new XMLHttpRequest();
    httprequest.open('POST','/leaveoperation/revertfullleaveinemployee.do',true);
    httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    httprequest.onreadystatechange =  function ()
    {
        if(httprequest.readyState == 4 && httprequest.status == 200)
        {
            var data = httprequest.responseText;
            document.getElementById("catagory_add_message").innerHTML = data;
        }

    };
    var load="empid="+encodeURIComponent(empid)+"&leaveid="+encodeURIComponent(leaveid)+"&applycriteria="+encodeURIComponent(applycriteria);
    httprequest.send(load);
}


function openPartialRevertLeave(empid,leaveid,applycriteria)
{
    var httprequest=new XMLHttpRequest();
    httprequest.open('POST','/leaveoperation/revertpartialleaveinemployee.do',true);
    httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    httprequest.onreadystatechange =  function ()
    {
        if(httprequest.readyState == 4 && httprequest.status == 200)
        {
            var data = httprequest.responseText;
            document.getElementById("placeholder").innerHTML = data;
        }

    };
    var load="empid="+encodeURIComponent(empid)+"&leaveid="+encodeURIComponent(leaveid)+"&applycriteria="+encodeURIComponent(applycriteria);
    httprequest.send(load);
}



function fetchEmployeeLeaveRecord(event)
{
    var code = event.keyCode;

    if(code == 13)
    {
        var empid=document.getElementById("employeeid").value;
        var httprequest=new XMLHttpRequest();
        httprequest.open('POST','/ajax/fetchemployeeleavebalanceapplication.do',true);
        httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
        httprequest.onreadystatechange =  function ()
        {
            if(httprequest.readyState == 4 && httprequest.status == 200)
            {
                var data = httprequest.responseText;
                document.getElementById("search_result").innerHTML = data;
            }

        };
        var load="empid="+encodeURIComponent(empid);
        httprequest.send(load);
    }
}


function fetchEmployeeLeaveRecordById(empid)
{

        var httprequest=new XMLHttpRequest();
        httprequest.open('POST','/ajax/fetchemployeeleavebalanceapplication.do',true);
        httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
        httprequest.onreadystatechange =  function ()
        {
            if(httprequest.readyState == 4 && httprequest.status == 200)
            {
                var data = httprequest.responseText;
                document.getElementById("search_result").innerHTML = data;
            }

        };
        var load="empid="+encodeURIComponent(empid);
        httprequest.send(load);
}


function fetchEmployeeAdvancedLeaveApp(event)
{
    var code = event.keyCode;

    if(code == 13)
    {
        var empid=document.getElementById("employeeid").value;
        var httprequest=new XMLHttpRequest();
        httprequest.open('POST','/ajax/fetchadvsearchcriteria.do',true);
        httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
        httprequest.onreadystatechange =  function ()
        {
            if(httprequest.readyState == 4 && httprequest.status == 200)
            {
                var data = httprequest.responseText;
                document.getElementById("search_result").innerHTML = data;

            }

        };
        var load="empid="+encodeURIComponent(empid);
        httprequest.send(load);
    }
}


function fetchEmpoyeeAdvancedLeaveApplication(empid)
{
    var httprequest=new XMLHttpRequest();
    httprequest.open('POST','/ajax/fetchadvsearchcriteria.do',true);
    httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    httprequest.onreadystatechange =  function ()
    {
        if(httprequest.readyState == 4 && httprequest.status == 200)
        {
            var data = httprequest.responseText;
            document.getElementById("search_result").innerHTML = data;
        }

    };
    var load="empid="+encodeURIComponent(empid);
    httprequest.send(load);
}


function applyAdvancedLeave()
{
   var searchcriteria=$('input[name=applycriteria]:checked').val();
   var minDate;
   var maxDate;
   if(searchcriteria == 1)
   {

       var thisYear = new Date();
       var month = thisYear.getMonth();
       if(month>=0 && month <= 5)
       {
           var thisYearDate = new Date();
           var previousYearDate = thisYearDate.getFullYear()-1;
           thisYearDate.setFullYear(previousYearDate);
           minDate=new Date(thisYearDate.getFullYear(),6,1);
           maxDate=new Date(thisYearDate.getFullYear(),11,31);
       }
       else
       {
           minDate=new Date(thisYear.getFullYear(),0,1);
           maxDate=new Date(thisYear.getFullYear(),5,30);
       }
   }
   else
   {

       var nextYear = new Date();
       var month = nextYear.getMonth();
       if(month >=0 && month <=5)
       {
           minDate=new Date(nextYear.getFullYear(),6,1);
           maxDate=new Date(nextYear.getFullYear(),11,31);
       }
       else
       {
           var nextYearValue = nextYear.getFullYear() + 1;
           nextYear.setFullYear(nextYearValue);
           minDate=new Date(nextYear.getFullYear(),0,1);
           maxDate=new Date(nextYear.getFullYear(),5,30);
       }

   }

    var empid=document.getElementById("empid").value;
    var httprequest=new XMLHttpRequest();
    httprequest.open('POST','/leaveoperation/fetchadvleaveapplicationform.do',true);
    httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    httprequest.onreadystatechange =  function ()
    {
        if(httprequest.readyState == 4 && httprequest.status == 200)
        {
            var data = httprequest.responseText;
            document.getElementById("search_result").innerHTML = data;
            $('#applicationDate').datepicker({dateFormat:"dd/mm/yy"});
            var today=new Date();
            var dd = today.getDate();
            var mm = today.getMonth()+1; //January is 0!

            var yyyy = today.getFullYear();
            if(dd<10){
                dd='0'+dd;
            }
            if(mm<10){
                mm='0'+mm;
            }
            var today = dd+'/'+mm+'/'+yyyy;
            $('#applicationDate').val(today);
            $('#fromDate').datepicker({
                    dateFormat : "dd/mm/yy",
                    minDate: minDate,
                    maxDate: maxDate
                }
            );
            $('#toDate').datepicker({dateFormat : "dd/mm/yy",
                minDate: minDate,
                maxDate: maxDate
            });
            $('#fromDate').on("change",function () {
                if($('#toDate').val() != '')
                {
                    var f = $('#fromDate').val();
                    var s = $('#toDate').val();
                    var no = daydiff(parseDate(f),parseDate(s))+1;
                    if(no <= 0 )
                    {
                        alert("To Date Can Not Be Less Than From Date");
                        $('#fromDate').val('');
                    }
                    else
                    {
                        calculateNumberofDays();
                        loadDayCalculation($('#fromDate').val(),$('#toDate').val());

                    }

                }

                else
                    $('#noofdays').val('0');
            })
            $('#toDate').on("change",function () {
                if($('#fromDate').val() != '')
                {
                    var f = $('#fromDate').val();
                    var s = $('#toDate').val();
                    var no = daydiff(parseDate(f),parseDate(s))+1;
                    if(no <= 0 )
                    {
                        alert("To Date Can Not Be Less Than From Date");
                        $('#toDate').val('');
                    }
                    else
                    {
                        calculateNumberofDays();
                        loadDayCalculation($('#fromDate').val(),$('#toDate').val());
                    }
                }

                else
                    $('#noofdays').val('0');
            })
        }

    };
    var load="empid="+encodeURIComponent(empid)+"&applycriteria="+encodeURIComponent(searchcriteria);
    httprequest.send(load);

}

function revertAdvancedLeave()
{
    var httprequest=new XMLHttpRequest();
    var empid=document.getElementById("empid").value;
    var applycriteria= $('input[name=applycriteria]:checked').val();
    httprequest.open('POST','/ajax/revertleavewithcriteria.do',true);
    httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    httprequest.onreadystatechange =  function ()
    {
        if(httprequest.readyState == 4 && httprequest.status == 200)
        {
            var data = httprequest.responseText;
            document.getElementById("search_result").innerHTML = data;

        }

    };
    var load="empid="+encodeURIComponent(empid)+"&applycriteria="+encodeURIComponent(applycriteria);
    httprequest.send(load);
}


function declareRH()
{
   var dateforrh = document.getElementById("datepicker").value;
   var catagory = document.getElementById("catagoryid").value;
    var httprequest=new XMLHttpRequest();
    httprequest.open('POST','/leaveoperation/declarerh.do',true);
    httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    httprequest.onreadystatechange =  function ()
    {
        if(httprequest.readyState == 4 && httprequest.status == 200)
        {
            var data = httprequest.responseText;
            if(data == "RH ALREADY DECLARED")
            {
                alert(data);
            }
            else
            {
                document.getElementById("placeholder").innerHTML = data;
            }


        }

    };
    var load="fromdate="+encodeURIComponent(dateforrh)+"&catagoryid="+encodeURIComponent(catagory);
    httprequest.send(load);
}


function loadDeleteEmployeeSearchResult(event)
{
    var code = event.keyCode;

    if(code == 13)
    {
        var empid=document.getElementById("employeeid").value;
        var httprequest=new XMLHttpRequest();
        httprequest.open('POST','/ajax/fetchdeletesearchcriteria.do',true);
        httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
        httprequest.onreadystatechange =  function ()
        {
            if(httprequest.readyState == 4 && httprequest.status == 200)
            {
                var data = httprequest.responseText;
                document.getElementById("search_result").innerHTML = data;
                $('#datepicker').datepicker({dateFormat:"dd/mm/yy"});
            }

        };
        var load="empid="+encodeURIComponent(empid);
        httprequest.send(load);
    }
}

function deleteemployee()
{
   var result = confirm("Do you really want to delete??");
   if(result)
   {
       var empid=document.getElementById("employeeid").value;
       var httprequest=new XMLHttpRequest();
       httprequest.open('POST','/employeeoperation/deleteemployee.do',true);
       httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
       httprequest.onreadystatechange =  function ()
       {
           if(httprequest.readyState == 4 && httprequest.status == 200)
           {
               var data = httprequest.responseText;
               alert(data);
           }

       };
       var load="empid="+encodeURIComponent(empid)+"&terminateddate="+encodeURIComponent($('#datepicker').val());
       httprequest.send(load);
   }

}


function applyUnrestrictedLWP()
{
   var employeeid=$('#employeeid').val();
   var fromdate = $('#datepicker').val();
   var todate = $('#datepicker1').val();

   if(employeeid == '')
   {
       alert("Kindly Specify The Employeeid");
       $('#employeeid').focus();
   }
   else
       if(fromdate == '')
       {
           alert("Kindly Specify The From Date");
           $('#datepicker').focus();
       }
       else
           if (todate == '')
           {
               alert("Kindly Specify The To Date");
               $('#datepicker1').focus();
           }
     else
           {
               unrestrictedLWP={};
               unrestrictedLWP["employeeid"]=employeeid;
               unrestrictedLWP["fromdate"]=fromdate;
               unrestrictedLWP["todate"]=todate;
               $.ajax({
                  url: "/leaveoperation/applyunrestrictedlwp.do",
                  type:"POST",
                  contentType:"application/json",
                   data:JSON.stringify(unrestrictedLWP),
                   timeout: 1000000,
                   success:function (data)
                   {
                       $('#catagory_add_message').html(data);
                       $('#employeeid').val('');
                       $('#datepicker').val('');
                       $('#datepicker1').val('');

                   }
               });
           }
}


function dissellectother(me) {
    var c = $(me).prop('checked');
    if (c==true) {
         if($(me.parentNode.nextElementSibling.firstChild).prop('checked') == true ||
             $(me.parentNode.nextElementSibling.nextElementSibling.firstChild).prop('checked') == true)
         {
           $('#noofdays').val(parseFloat($('#noofdays').val())+0.5);
         }
        $(me.parentNode.nextElementSibling.firstChild).attr('checked',false);
        $(me.parentNode.nextElementSibling.nextElementSibling.firstChild).attr('checked',false);
    }
    else
    {
        $(me).prop('checked',true);
    }


}

function dissellectother1(me)
{
    var c = $(me).prop('checked');
    if (c==true) {
        if($(me.parentNode.previousElementSibling.firstChild).prop('checked') == true)
        {
            $('#noofdays').val(parseFloat($('#noofdays').val())-0.5);
        }
        $(me.parentNode.nextElementSibling.firstChild).attr('checked', false);
        $(me.parentNode.previousElementSibling.firstChild).attr('checked', false);
    }
    else
    {
        $('#noofdays').val(parseFloat($('#noofdays').val())+0.5);
        $(me.parentNode.previousElementSibling.firstChild).prop('checked', 'checked');

    }
}

function dissellectother2(me)
{
    var c = $(me).prop('checked');
    if (c==true) {
        if($(me.parentNode.previousElementSibling.previousElementSibling.firstChild).prop('checked') == true)
        {
            $('#noofdays').val(parseFloat($('#noofdays').val())-0.5);
        }
        $(me.parentNode.previousElementSibling.firstChild).attr('checked', false);
        $(me.parentNode.previousElementSibling.previousElementSibling.firstChild).attr('checked', false);
    }
    else
    {
        $('#noofdays').val(parseFloat($('#noofdays').val())+0.5);
        $(me.parentNode.previousElementSibling.previousElementSibling.firstChild).prop('checked', true);
    }

}


function processborrow(empid,leaveid)
{
    var httprequest=new XMLHttpRequest();
    httprequest.open('POST','/ajax/borrow.do',true);
    httprequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    httprequest.onreadystatechange=function ()
    {
        if(httprequest.readyState == 4 && httprequest.status == 200)
        {
            document.getElementById("search_result").innerHTML=httprequest.responseText;
        }
    }
    var load="empid="+encodeURIComponent(empid)+"&leaveid="+encodeURIComponent(leaveid);
    httprequest.send(load);
}






