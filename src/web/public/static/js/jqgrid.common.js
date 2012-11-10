var gridData = new Array();
var searchData = new Array();
var gridHeader = new Array();

function getHeaderColumns() {
	var cols = $(".dl-grid-header-column");
	for ( var i = 0; cols[i]; i++) {
		gridHeader[i] = normalizeHeaderName(cols[i].innerHTML);
	}
}

function normalizeHeaderName(colTitle) {
	return colTitle.replace(/ /g, "_").replace(/&/g, "&amp;").replace(/</g,
			"&lt;").replace(/>/g, "&gt;");
}

function gridSearch(searchString) {
	searchData = [];
	if ($("#searchColumn").val() == "all") {
		searchAllColumns(searchString);
	} else {
		searchOneColumn(searchString, $("#searchColumn").val());
	}
	$("#grid").jqGrid('clearGridData', true);
	$("#grid").jqGrid('setGridParam', {
		data : searchData
	}).trigger("reloadGrid");

}


function searchAllColumns(searchString) {
	if (searchString != "") {
		var c = 0;
		var obj = gridHeader;
		for ( var i = 0; i < gridData.length; i++) {
			for ( var k = 0; k < obj.length; k++) {
				var toCmp = gridData[i][obj[k]];
				if ((toCmp != undefined)
						&& (toCmp.toLowerCase().indexOf(searchString) != -1)) {
					searchData[c] = gridData[i];
					c++;
					k = obj.length;
				}
			}
		}
	} else
		searchData = gridData;
}

function searchOneColumn(searchString, searchColumn) {
	if (searchString != "") {
		var p = normalizeHeaderName(searchColumn);
		var c = 0;
		for ( var i = 0; i < gridData.length; i++) {
			var toCmp = gridData[i][p];
			if ((toCmp != undefined)
					&& (toCmp.toLowerCase().indexOf(searchString) != -1)) {
				searchData[c] = gridData[i];
				c++;
			}
		}
	} else
		searchData = gridData;
}

function htmlTableTojQGrid(params) {
		getHeaderColumns();
		jQuery.extend(jQuery.jgrid.defaults, {
			caption: $('#searchnav').html(),
			rowNum: 10000,
			height: 450,
			search:{
				caption: '#i18n("grid.search.value.label")'
			},
		});
		
		var gridParams = { 
	 			datatype: "local", 
	 			pager: $('#pagernav'), 
	 			rowNum: 500,
				rowList: [50,100,200,500,1000,2000],
		};
		if (params != null) {
			gridParams = jsonConcat(params, gridParams);
		}
		tableToGrid("#grid", gridParams);
		$("#grid").trigger("reloadGrid");
		gridData = $("#grid").getRowData();
		// Function called when modifying search text
		$("#gridsearch").keyup(function () {
			gridSearch($(this).val().toLowerCase());
		});
		// Function called when modifying search Column
		$("#searchColumn").change(function() {
			gridSearch($("#gridsearch").val().toLowerCase());
		});
}

function jsonConcat(o1, o2) {
	for ( var key in o2) {
		o1[key] = o2[key];
	}
	return o1;
}
