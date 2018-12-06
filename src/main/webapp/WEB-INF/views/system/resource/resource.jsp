<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    var resourceTreeGrid;
    $(function() {
        resourceTreeGrid = $('#resourceTreeGrid').treegrid({
            url : '${path }/resource/treeGrid',
            idField : 'id',
            treeField : 'name',
            parentField : 'pid',
            fit : true,
            border : false,
            frozenColumns : [ [ {
                title : '编号',
                field : 'id',
                width : 40
            } ] ],
            columns : [ [ {
                field : 'name',
                title : '资源名称',
                width : 150
            }, {
                field : 'url',
                title : '资源路径',
                width : 300
            }, {
                field : 'openMode',
                title : '打开方式',
                align : 'center',
                width : 100
            }, {
                field : 'opened',
                title : '菜单状态',
                align : 'center',
                width : 60,
                formatter : function(value, row, index) {
                    if (value == "opend") {
                        return '打开';
                    } else {
                        return '关闭';
                    }
                }
            }, {
                field : 'sort',
                title : '排序',
                align : 'center',
                width : 40
            }, {
                field : 'iconCls',
                title : '图标',
                align : 'center',
                width : 200
            }, {
                field : 'resourceType',
                title : '资源类型',
                align : 'center',
                width : 80,
                formatter : function(value, row, index) {
                    switch (value) {
                    case 0:
                        return '菜单';
                    case 1:
                        return '按钮';
                    default:
						break;
                    }
                }
            }, {
                field : 'pid',
                title : '上级资源ID',
                width : 150,
                hidden : true
            }, {
                field : 'status',
                title : '状态',
                align : 'center',
                width : 40,
                formatter : function(value, row, index) {
                    switch (value) {
                    case 0:
                        return '正常';
                    case 1:
                        return '停用';
                    default:
						break;
                    }
                }
            }, {
                field : 'action',
                title : '操作',
                align : 'center',
                width : 130,
                formatter : function(value, row, index) {
                    var str = '';
                    <shiro:hasPermission name="/resource/edit">
                        str += $.formatString('<a href="javascript:void(0)" class="resource-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'glyphicon-pencil icon-blue\'" onclick="editResourceFun(\'{0}\');" >编辑</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/resource/delete">
                        str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                        str += $.formatString('<a href="javascript:void(0)" class="resource-easyui-linkbutton-del" data-options="plain:true,iconCls:\'glyphicon-trash icon-red\'" onclick="deleteResourceFun(\'{0}\');" >删除</a>', row.id);
                    </shiro:hasPermission>
                    return str;
                }
            } ] ],
            onLoadSuccess:function(data){
                $('.resource-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.resource-easyui-linkbutton-del').linkbutton({text:'删除'});
            },
            toolbar : '#resourceToolbar'
        });
    });

    function editResourceFun(id) {
        if (id != undefined) {
            resourceTreeGrid.treegrid('select', id);
        }
        var node = resourceTreeGrid.treegrid('getSelected');
        if (node) {
            parent.$.modalDialog({
                title : '编辑',
                width : 500,
                height : 350,
                href : '${path }/resource/editPage?id=' + node.id,
                buttons : [ {
                    text : '确定',
                    handler : function() {
                        parent.$.modalDialog.openner_treeGrid = resourceTreeGrid;
                        var f = parent.$.modalDialog.handler.find('#resourceEditForm');
                        f.submit();
                    }
                } ]
            });
        }
    }

    function deleteResourceFun(id) {
        if (id != undefined) {
            resourceTreeGrid.treegrid('select', id);
        }
        var node = resourceTreeGrid.treegrid('getSelected');
        if (node) {
            parent.$.messager.confirm('询问', '删除当前资源会连同子资源一起删除,您是否要删除当前资源？', function(b) {
                if (b) {
                    progressLoad();
                    $.post('${path }/resource/delete', {
                        id : node.id
                    }, function(result) {
                        progressClose();
                        showMsg(result.msg);
                        if (result.code == "000") {
                            resourceTreeGrid.treegrid('reload');
                            console.log(parent.layoutWestTree);
                            parent.layout_west_tree.tree('reload');
                        }
                    }, 'JSON');
                }
            });
        }
    }

    function addResourceFun() {
        parent.$.modalDialog({
            title : '添加',
            width : 500,
            height : 350,
            href : '${path }/resource/addPage',
            buttons : [ {
                text : '添加',
                handler : function() {
                    parent.$.modalDialog.openner_treeGrid = resourceTreeGrid;
                    var f = parent.$.modalDialog.handler.find('#resourceAddForm');
                    f.submit();
                }
            } ]
        });
    }
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:false"  style="overflow: hidden;">
        <table id="resourceTreeGrid"></table>
    </div>
</div>
<div id="resourceToolbar" style="display: none;">
    <shiro:hasPermission name="/resource/add">
        <a onclick="addResourceFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'glyphicon-plus icon-green'">添加</a>
    </shiro:hasPermission>
</div>