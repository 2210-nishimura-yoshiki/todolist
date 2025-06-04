$(function () {
    //現在の日付を取得
    var today = new Date();

    for (var i=0; i<$('.tasks').length; i++){
        var index = i;
        // index番号のクラスを追加
        $("#" + index).find(".strLimitDate").attr('id', 'strLimitDate' + index);
        $("#" + index).find(".select_button").attr('id', 'select_button' + index);
        $("#" + index).find(".select_box").attr('id', 'select_box' + index);
        $("#" + index).find(".select_form").attr('id', 'select_form' + index);

        var strLimitDate = $('#strLimitDate' + index).text();
        var limitDate = Date.parse(strLimitDate);

         if (limitDate == today || limitDate < today) {
             $('#strLimitDate' + index).css('background','red');
         } else {
             $('#strLimitDate' + index).css('background','yellow');
         }
    }
    //セレクトボックス0が切り替わったらフォーム0を送信
//    $('#select_box0').change(function() {
//        $('#select_button0').trigger('click');
//        $("#select_form0").submit();
//    });

    // セレクトボックスが変更された場合、フォームのIDを取得してそのフォームの「submit」する。
    $('.select_box').change(function() {
    	let index = $(this).parent().attr('id');
    	$("#" + index).submit();
    });


//    $('.select_box').change(function() {
////        $('#select_button0').trigger('click');
//        $(".select_form").submit();
//    });
})



function inputChange(i){
//    var index = document.getElementById("#tasks")
    document.getElementById("#select_form" + i).submit();
}