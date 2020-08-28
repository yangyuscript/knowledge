function mineRequest(url,param,succFun,errFun){
    $.ajax({
        url: url,
        type: 'post',
        data: param,
        dataType: 'json',
        headers: {
            token:localStorage.getItem('mind-token')
        },
        success: function (res) {
            console.log(res);
            succFun(res);
        },
        error: function (xhr, textStatus, errorThrown) {
            //alert(xhr.getResponseHeader('needLogin'));
            errFun(xhr.getResponseHeader('needLogin'))
        }
    })
}

function mineGetRequest(url,param,succFun){
    $.ajax({
        url: url,
        type: 'get',
        data: param,
        dataType: 'json',
        headers: {
            token: localStorage.getItem('mind-token')
        },
        success: function(res){
            console.log(res);
            succFun(res);
        }
    })
}