function parseDate(str) {
    var mdy = str.split('/');
    return new Date(mdy[2], mdy[1]-1, mdy[0]);
}

function daydiff(first, second) {
    return Math.round((second-first)/(1000*60*60*24));
}

function calculateNumberofDays()
{
   var first = $('#fromDate').val();
   var second = $('#toDate').val();
   document.getElementById("noofdays").value=daydiff(parseDate(first),parseDate(second))+1;
}

function processHalfDay()
{
    var first = $('#fromDate').val();
    var second = $('#toDate').val();
   if($('#halfday').is(':checked'))
   {
       $('#noofdays').val(parseFloat(daydiff(parseDate(first),parseDate(second))+1)-0.5);
   }
   else
   {
       $('#noofdays').val(parseFloat(daydiff(parseDate(first),parseDate(second))+1));
   }
}