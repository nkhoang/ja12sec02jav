<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="webapp.title"/></title>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
    <script type="text/javascript" src="<c:url value='/js/ext-all.js' />"></script>
    <link href="<c:url value='/resources/css/ext-all-gray.css'/>" rel="stylesheet" media="all"/>
    <script type="text/javascript">
        var dictionaryStore;// define model for Dictionary
        $(function() {
            Ext.define('Dictionary', {
                extend: 'Ext.data.Model',
                fields: [
                    'id',
                    'name',
                    'description'
                ]
            });

            dictionaryStore = Ext.create('Ext.data.Store', {
                model: 'Dictionary',
                proxy: {
                    type: 'ajax',
                    url: '<c:url value="/services/app/dictionary" />',
                    reader: {
                        type: 'json',
                        root: 'data'
                    },
                    writer: {
                        type: 'json'
                    }
                },
                autoLoad: true,
                autoSync: true,
                listeners: {
                    write: function(store, operation) {
                        var record = operation.getRecords()[0],
                                name = Ext.String.capitalize(operation.action),
                                verb;


                        if (name == 'Destroy') {
                            record = operation.records[0];
                            verb = 'Destroyed';
                        } else {
                            verb = name + 'd';
                        }
                        Ext.example.msg(name, Ext.String.format("{0} user: {1}", verb, record.getId()));

                    }
                }
            });
            var grid = Ext.create('Ext.grid.Panel', {
                store: dictionaryStore,
                columns: [
                    {
                        header: 'Name',
                        dataIndex: 'name',
                        flex: 1,
                        editor: {
                            // defaults to textfield if no xtype is supplied
                            allowBlank: false
                        }
                    },
                    {
                        header: 'Description',
                        dataIndex: 'description',
                        flex: 1,
                        editor: {
                            // defaults to textfield if no xtype is supplied
                            allowBlank: false
                        }
                    }
                ],
                renderTo: 'dict-grid-container',
                width: 600,
                height: 400,
                title: 'Available Dictionary',
                frame: true,
                tbar: [
                    {
                        text: 'Add Dictionary',
                        iconCls: 'employee-add',
                        handler : function() {
                            rowEditing.cancelEdit();

                            // Create a model instance
                            var r = Ext.create('Dictionary', {
                                name: '',
                                description: ''
                            });
                            store.insert(0, r);
                            rowEditing.startEdit(0, 0);
                        }
                    },
                    {
                        itemId: 'removeDictionary',
                        text: 'Remove Dictionary',
                        iconCls: 'employee-remove',
                        handler: function() {
                            var sm = grid.getSelectionModel();
                            rowEditing.cancelEdit();
                            store.remove(sm.getSelection());
                            if (store.getCount() > 0) {
                                sm.select(0);
                            }
                        },
                        disabled: true
                    }
                ],
                plugins: [Ext.create('Ext.grid.plugin.RowEditing', {
                    clicksToMoveEditor: 1,
                    autoCancel: false
                })],
                listeners: {
                    'selectionchange': function(view, records) {
                        grid.down('#removeDictionary').setDisabled(!records.length);
                    }
                }
            });
        });
    </script>
</head>
<body>
<div id="dict-grid-container"></div>
</body>
</html>