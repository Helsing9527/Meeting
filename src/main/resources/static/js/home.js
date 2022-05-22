$(function () {
    new Vue({
        el: "#app",
        data() {
            return {
                index: '1',
                value: new Date(),
                // 用户名
                fit: '',
                // 用户头像
                url: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg',
                tableData: [],
                currentPage: ""
            };
        },
        methods: {
            // 导航内容切换
            setIndex(val) {
                this.index = val;
            },
            // 会议申请
            meetingApply() {
                this.setIndex(2);
                axios.get("/meeting").then((res)=>{
                    this.tableData = res.data.data;
                })
            },
            // 表格选中
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
            },
            // 分页
            handleSizeChange(val) {
                console.log(`每页 ${val} 条`);
            },
            handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
            }
        },
        mounted() {
            axios.get("/meeting/userInfo/" + window.location.search.substr(1)).then((res) => {
                this.fit = res.data.data.name;
            })
        }
    });
})