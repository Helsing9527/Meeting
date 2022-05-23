$(function () {
    new Vue({
        el: "#app",
        data() {
            return {
                // 默认显示欢迎页
                index: '1',
                // 欢迎页日历
                value: new Date(),
                // 用户头像
                url: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg',
                // 会议申请 表格
                tableData: [],
                // 会议申请 分页工具条
                pagination: {//分页相关模型数据
                    currentPage: 1,//当前页码
                    pageSize: 10,//每页显示的记录数
                    total: 0,//总记录数
                    name: '',
                    dept: '',
                    post: '',
                    gender: ''
                }
            };
        },
        methods: {
            // 导航内容切换
            setIndex(val) {
                this.index = val;
            },
            // 会议申请列表
            meetingApply() {
                this.getAll();
                this.setIndex(2);
            },
            // 查询数据库所有人回填列表
            getAll() {
                axios.get("/meeting/" + this.pagination.currentPage + "/" + this.pagination.pageSize).then((res) => {
                    this.tableData = res.data.data.records;
                    this.pagination.currentPage = res.data.data.current;
                    this.pagination.pageSize = res.data.data.size;
                    this.pagination.total = res.data.data.total;
                    console.log(res.data)
                })
            },
            // 变色表格
            tableRowClassName({row, rowIndex}) {
                if (rowIndex % 3 == 1) {
                    return 'warning-row';
                } else if (rowIndex % 3 != 2) {
                    return 'success-row';
                }
                return '';
            },
            // 表格选中
            handleSelectionChange(val) {
                this.multipleSelection = val;
                console.log(val)
            },
            // 分页
            handleSizeChange(val) {
                this.pagination.pageSize = val;
                this.getAll();
            },
            handleCurrentChange(val) {
                this.pagination.currentPage = val;
                this.getAll();
            }
        }
    });
})