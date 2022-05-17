$(function () {
    new Vue({
        el: "#app",
        data() {
            return {
                index: '',
                value: new Date(),
                // 用户名
                fit: '用户名',
                // 用户头像
                url: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg',
                tableData: []
            };
        },
        methods: {
            tableRowClassName({row, rowIndex}) {
                if (rowIndex === 1) {
                    return 'warning-row';
                } else if (rowIndex === 3) {
                    return 'success-row';
                }
                return '';
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
                console.log(val)
            }
        }
    })
})