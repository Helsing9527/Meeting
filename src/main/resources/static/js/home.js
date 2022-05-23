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
                url: 'https://cn.vuejs.org/images/logo.svg',
                // 会议申请 人员表格
                tableData: [],
                // 会议申请 分页工具条
                pagination: {//分页相关模型数据
                    currentPage: 1,//当前页码
                    pageSize: 10,//每页显示的记录数
                    total: 0,//总记录数
                    name: '',//姓名
                    dept: '',//部门
                    post: '',//职位
                    gender: ''//性别
                },
                // 人员确认后临时存储
                multipleSelection: [],
                // 会议申请 表单/表单校验
                ruleForm: {
                    meetingName: '',//会议名称
                    meetingPlace: '',//会议地点
                    date: '',//会议日期
                    desc: '',//会议内容
                    persons: []
                },
                rules: {
                    meetingName: [
                        {required: true, message: '请输入会议名称', trigger: 'blur'},
                        {min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur'}
                    ],
                    meetingPlace: [
                        {required: true, message: '请选择会议室', trigger: 'change'}
                    ],
                    date: [
                        {type: 'date', required: true, message: '请选择日期', trigger: 'change'}
                    ],
                    desc: [
                        {required: true, message: '请填写会议内容', trigger: 'blur'}
                    ],
                    persons: [
                        {required: true, message: '请选择参会人员', trigger: 'input'}
                    ]
                },
                //选择会议人员对话框
                personsDialogVisible: false
            };
        },
        methods: {
            // 导航二级菜单
            handleOpen(key, keyPath) {
                // console.log(key, keyPath);
            },
            handleClose(key, keyPath) {
                // console.log(key, keyPath);
            },
            // 导航内容切换
            setIndex(val) {
                this.index = val;
            },
            // 会议申请 人员列表
            selectPersons() {
                this.getAll();
                this.personsDialogVisible = true;
            },
            // 分页查询数据库人员并回填列表
            getAll() {
                axios.get("/meeting/" + this.pagination.currentPage + "/" + this.pagination.pageSize).then((res) => {
                    this.tableData = res.data.data.records;
                    this.pagination.currentPage = res.data.data.current;
                    this.pagination.pageSize = res.data.data.size;
                    this.pagination.total = res.data.data.total;
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
            // 人员列表选择人员
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            // 人员列表选中确认后遍历获取所有id，并赋值给表单等待提交
            selectPerson() {
                var val = this.multipleSelection;
                for (let i = 0; i < val.length; i++) {
                    this.ruleForm.persons.push(val[i].id);
                }
                this.personsDialogVisible = false;
            },
            // 分页
            handleSizeChange(val) {
                this.pagination.pageSize = val;
                this.getAll();
            }
            ,
            handleCurrentChange(val) {
                this.pagination.currentPage = val;
                this.getAll();
            }
            ,
            // 会议申请表单
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        axios.post("/meeting").then((res) => {
                            if (res.data.flag) {
                                this.$message.success(res.data.msg);
                            }
                        })
                        console.log(this.ruleForm)
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            }
            ,
            resetForm(formName) {
                this.$refs[formName].resetFields();
            }
        }
    });
})