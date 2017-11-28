function openAddCatagoryForm(url)
{
   url="/ajax/" + url;
   $('#placeholder').load(url);
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

sendDataForSingleEmployeeSearch(searchall,document.getElementById("employeeid").value);
}


function sendDataForSingleEmployeeSearch(search,empid)
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
    var load = searchall+"&empid="+empid;
    httprequest.send(load);
}


function fetchEmployeeRecord()
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
        }

    };
    var load="empid="+encodeURIComponent(empid);
    httprequest.send(load);
}



