// 登录之后访问方法
function authAjax(jQuery, url, data, callback) {
    jQuery.ajax({
        url: url,
        type: 'POST',
        headers: {
            'Authorization': layui.data('login_user_info_key').authorization
        },
        data: data,
        success: callback
    });
}