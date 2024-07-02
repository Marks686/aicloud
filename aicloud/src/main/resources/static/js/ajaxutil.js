// 登录之后访问方法
function authAjax(jQuery, url, data, callback) {
    jQuery.ajax({
        url: url,
        type: 'POST',
        headers: {
            'Authorization': layui.data('aicloud_jwt').authorization
        },
        data: data,
        success: callback
    });
}