$('[data-toggle="tooltip"]').tooltip();

function openModalSchedule(e) {
	var form = $('#modalschedule form');
	form.find('input[name="new"]').val('true');
	form.find('[name="name"]').val('');
	form.find('[name="tracker"]').val('');
	loadTrackerUsersSelect('');
	form.find('[name="trackerUser"]').val('');
	form.find('[name="search"]').val('');
	form.find('[name="category"]').val('');
	form.find('[name="email"]').val('');
	form.find('[name="startDate"]').val('');
	form.find('[name="interval"]').val('');
	form.find('[name="template"]').val('');
	$('#div-torrent-filter-schedule .table-torrent-filter').html('');
	
	var tracker = $(this).data('tracker');
	if (tracker) {
		form.find('[name="tracker"]').val(tracker);
		loadTrackerUsersSelect(tracker);
		loadTrackerCategoriesSchedule(tracker).done(function() {
			var trackercategoriesVal = $("#trackercategories").val();
			var cats = [];
			if (trackercategoriesVal) {
				for (var i=0; i<trackercategoriesVal.length; i++) {
					cats.push(trackercategoriesVal[i].split('|')[1]);
				}
			}
			$("#trackercategoriesschedule").val(cats).trigger('change');
		});
		form.find('[name="tracker"]').attr('readonly', 'readonly');
	
		form.find('[name="search"]').val($('#formfilter [name="search"]').val());
		$('#div-torrent-filter-schedule .table-torrent-filter').html($('#div-torrent-filter .table-torrent-filter').html());
		if ($('#div-torrent-filter-schedule .table-torrent-filter').html() != '') {
			var filterschedule = $('#div-torrent-filter-schedule .table-torrent-filter > tbody > tr');
			$('#div-torrent-filter .table-torrent-filter > tbody > tr').each(function(idx, obj) {
				var tr = $(obj);
				var trSchedule = $(filterschedule[idx]);
				trSchedule.find('[name="torrentfilteroperation"]').val(tr.find('[name="torrentfilteroperation"]').val());
				trSchedule.find('[name="torrentfiltervalue"]').val(tr.find('[name="torrentfiltervalue"]').val());
			});
			$('#div-torrent-filter-schedule').show();
		}
	}
	else {
		form.find('[name="tracker"]').removeAttr('readonly');
	}
	$('#modalschedule').modal('show');
}

$('.modalschedule').on('click', openModalSchedule);

$('#divtrackerresults').on('click', '.modalschedule', openModalSchedule);

$('.modaltrackeruser').on('click', function(e) {
	var form = $('#modaltrackeruser form');
	form.find('input[name="new"]').val('true');
	form.find('[name="tracker"]').val('').trigger('change');
	form.find('[name="username"]').val('');
	form.find('[name="password"]').val('');
	form.find('[name="captcha"]').val('');
	form.find('[name="shared"]').prop('checked', false);
	$('#modaltrackeruser').modal('show');
});

$('.modalseedbox').on('click', function(e) {
	var form = $('#modalseedbox form');
	form.find('input[name="new"]').val('true');
	form.find('[name="name"]').val('');
	form.find('[name="url"]').val('');
	form.find('[name="username"]').val('');
	form.find('[name="password"]').val('');
	form.find('[name="label"]').val('');
	form.find('[name="webClient"]').val('');
	$('#modalseedbox').modal('show');
});

$('.modaluser').on('click', function(e) {
	var form = $('#modaluser form');
	form.find('input[name="new"]').val('true');
	form.find('[name="username"]').val('');
	form.find('[name="password"]').val('');
	form.find('[name="admin"]').prop('checked', false);
	$('#modaluser').modal('show');
});

$('#divtrackerresults').on('click', '.modalpickseedbox', function(e) {
	var tr = $(e.currentTarget).parents('tr');
	$('#modalpickseedbox form [name="torrent"]').val(decodeURI($(tr[0]).data('torrent')));
	if ($('#modalpickseedbox form [name="seedbox"] option').length > 1) {
		$('#modalpickseedbox').modal('show');
	}
	else if ($('#modalpickseedbox form [name="seedbox"] option').length <= 0) {
		alert('No seedboxes avaliables');
	}
	else {
		$('#modalpickseedbox form [name="seedbox"]').val($('#modalpickseedbox form [name="seedbox"] option').val());
		if (confirm('Are you sure?')) {
			$('#modalpickseedbox form button[type="submit"]').trigger('click');
		}
	}
	
	
});

function buildTorrentDetail(data) {
	if (!data.success) {
		alert('Problem when loading data, try again');
	}
	else {
		var torrent = data.data;
		if (torrent) {
			$('#rls-name').html(torrent.name);
			$('#rls-img').prop('src', '');
			$('#rls-title').html(torrent.name);
			$('#rls-rating').html('');
			$('#rls-plot').html('');
			var iframe = $('#rls-content');
			iframe.ready(function() {
				iframe.contents().find("body").html('');
				iframe.contents().scrollTop(0);
			    iframe.contents().scrollLeft(0);
			    iframe.contents().find("body").html(torrent.content);
			});
		}
		var movie = torrent.movie;
		if (movie && movie.title) {
			$('#modaltorrentdetail div.media').show();
			$('#rls-img').prop('src', 'LoadImageImdb.vnt?image=' + encodeURIComponent(movie.poster));
			$('#rls-title').html(movie.title + ' (' + movie.year + ')');
			$('#rls-rating').html('<div class="pull-right">' + movie.imdbRating + '/10 (' + movie.imdbVotes + ' votes)</div>');
			$('#rls-plot').html(movie.plot);
		}
		else {
			$('#modaltorrentdetail div.media').hide();
		}
		
		$('#modaltorrentdetail').modal('show');
	}
	
}

function loadTorrentContentNewWindow() {
	var w = window.open('', '_blank');
	var iframe = $('#rls-content');
	w.document.write(iframe.contents().find("body").html());
}

$('#rls-content-nw-btn').on('click', loadTorrentContentNewWindow);

function loadTorrentDetail(e) {
	var btn = $(e.currentTarget);
	var tr = btn.parents('tr');
	btn.removeClass('fa fa-info-circle');
	btn.addClass('fa fa-spinner fa-spin');
	var torrent = JSON.parse(decodeURI($(tr[0]).data('torrent')));
	torrent.content = '';
	torrent.detailed = false;
	$.getJSON('LoadTorrentDetail.vnt?torrent='+ encodeURIComponent(JSON.stringify(torrent))).done(buildTorrentDetail).always(function() {
		btn.removeClass('fa fa-spinner fa-spin');
		btn.addClass('fa fa-info-circle');
	});
	
}

$('#divtrackerresults').on('click', '.modaltorrentdetail', loadTorrentDetail);

$('#modalpickseedbox form').on('submit', function(e) {
	e.preventDefault();
	var form = $(this);
	var btn = $('#divtrackerresults tr[data-torrent="' + encodeURI(form.find('[name="torrent"]').val()) + '"] .modalpickseedbox');
	
	btn.removeClass('glyphicon glyphicon-info-sign');
	btn.addClass('fa fa-spinner fa-spin');
	$.getJSON('UploadTorrentToSeedbox.vnt?c=false&' + form.serialize()).done(function(data) {
		if (data.success) {
			alert('Sent with success');
		}
		else {
			alert('Problem when sending the torrent');
		}
	}).always(function() {
		btn.addClass('glyphicon glyphicon-info-sign');
		btn.removeClass('fa fa-spinner fa-spin');
	});
	
	return false;
});

function loadTrackerUsers() {
	$.getJSON('LoadTrackerUsers.vnt')
			.done(
					function(data) {
						var tbody = $('#trackeruserstable > tbody');
						tbody.html('');
						for (var i = 0; i < data.length; i++) {
							var trackerUser = data[i];
							var html = '';
							html += '<tr data-id="' + (trackerUser.owned ? trackerUser.id : 0) + '" data-tracker="' + trackerUser.tracker + '" data-trackerclass="' + trackerUser.trackerClass + '"  data-username="' + trackerUser.username + '" data-shared="' + trackerUser.shared + '" data-owned="' + trackerUser.owned + '">';
							html += '<td>' + trackerUser.tracker + '</td>';
							html += '<td>' + trackerUser.username + '</td>';
							html += '<td>' + (trackerUser.owned ? (trackerUser.shared?'Yes':'No') : 'Owner: ' + trackerUser.owner) + '</td>';
							html += '<td>' + (trackerUser.owned ? '<i class="glyphicon glyphicon-remove" style="color:red;"></i>' : '') + '</td>';
							html += '</tr>';
							tbody.append(html);
						}
					});
}

$('a[href="#trackerusers"]').on('show.bs.tab', function(e) {
	loadTrackerUsers();
});

$('#modaltrackeruser form [name="tracker"]').on('change', function(e) {
	if ($('#modaltrackeruser form [name="tracker"] option:selected').data('hascaptcha')) {
		$('#modaltrackeruser form [name="captcha"]').parent().show();
		$('#btnLogin').show();
	}
	else {
		$('#modaltrackeruser form [name="captcha"]').parent().hide();
		$('#btnLogin').hide();
	}
});

$('#modaltrackeruser form').on('submit', function(e) {
	e.preventDefault();
	var form = $(this);
	$.post('SaveTrackerUsers.vnt', form.serialize()).done(function(data) {
		if (data.success) {
			loadTrackerUsers();
			loadTrackers();
			$('#modaltrackeruser').modal('hide');
		}
		else {
			alert('Problem when saving data');
			console.log(data.error);
		}
	});
	return false;
});

$('#btnLogin').on('click', function(e) {
	var form = $('#modaltrackeruser form');
	var btn = $(this);
	
	btn.button('loading');
	$.post('LoginTrackerUser.vnt', form.serialize()).done(function(data) {
		if (data.success) {
			alert('Success');
			$('#modaltrackeruser form button[type="submit"]').trigger('click');
		}
		else {
			alert('Problem with login');
			console.log(data.error);
		}
	}).always(function() {
		btn.button('reset');
	});
	return false;
});

$('#trackeruserstable').on('click', 'tr td:last-child', function(e) {
	var tr = $(this).parents('tr');
	var id = $(tr).data('id');
	if (id) {
		$.ajax('SaveTrackerUsers.vnt?id=' + id, {
			method : 'DELETE'
		}).done(function(data) {
			if (data.success) {
				loadTrackerUsers();
				loadTrackers();
			}
		});
	}
});

$('#trackeruserstable').on('click', 'tr td:not(:last-child)', function(e) {
	var tr = $(this).parents('tr');
	var form = $('#modaltrackeruser form');
	var id = $(tr).data('id');
	var pktracker = $(tr).data('tracker');
	var pkusername = $(tr).data('username');
	var trackerclass = $(tr).data('trackerclass');
	var shared = $(tr).data('shared');
	var owned  = $(tr).data('owned');
	if (owned) {
		form.find('input[name="new"]').val('false');
		form.find('input[name="id"]').val(id);
		form.find('input[name="pktracker"]').val(pktracker);
		form.find('input[name="pkusername"]').val(pkusername);
		form.find('[name="tracker"]').val(trackerclass).change();
		form.find('[name="username"]').val(pkusername);
		form.find('[name="shared"]').prop('checked', shared);
		$('#modaltrackeruser').modal('show');
	}
});


function loadSeedboxes() {
	$.getJSON('LoadSeedboxes.vnt')
			.done(
					function(data) {
						var pickseedboxselect = $('#modalpickseedbox form [name="seedbox"');
						pickseedboxselect.html('');
						var tbody = $('#seedboxestable > tbody');
						tbody.html('');
						for (var i = 0; i < data.length; i++) {
							var seedbox = data[i];
							var html = '';
							html += '<tr data-id="' + seedbox.id + '">';
							html += '<td>' + seedbox.name + '</td>';
							html += '<td>' + seedbox.username + '</td>';
							var webClient = '';
							if (seedbox.webClient) {
								var wcSplit = seedbox.webClient.split('.');
								webClient = wcSplit[wcSplit.length - 1];
							}
							html += '<td>' + webClient + '</td>';
							html += '<td><i class="glyphicon glyphicon-remove" style="color:red;"></i></td>';
							html += '</tr>';
							tbody.append(html);
							
							pickseedboxselect.append('<option value="' + seedbox.id + '">' + seedbox.name + '</option>');
						}
					});
}

$('a[href="#seedboxes"]').on('show.bs.tab', function(e) {
	loadSeedboxes();
})

$('#modalseedbox form').on('submit', function(e) {
	e.preventDefault();
	var form = $(this);
	$.post('SaveSeedboxes.vnt', form.serialize()).done(function(data) {
		if (data.success) {
			loadSeedboxes();
			$('#modalseedbox').modal('hide');
		}
		else {
			alert('Problem when saving data');
			console.log(data.error);
		}
	});
	return false;
});

$('#seedboxestable').on('click', 'tr td:last-child', function(e) {
	var tr = $(this).parents('tr');
	var pk = $(tr).data('id');
	$.ajax('SaveSeedboxes.vnt?id=' + pk, {
		method : 'DELETE'
	}).done(function(data) {
		if (data.success) {
			loadSeedboxes();
		}
	});
});

$('#seedboxestable').on('click', 'tr td:not(:last-child)', function(e) {
	var tr = $(this).parents('tr');
	var form = $('#modalseedbox form');
	var pk = $(tr).data('id');
	form.find('input[name="new"]').val('false');
	form.find('input[name="id"]').val(pk);
	$.getJSON('LoadSeedboxes.vnt?id=' + pk).done(function(data){
		var seedbox = data;
		
		form.find('[name="name"]').val(seedbox.name);
		form.find('[name="url"]').val(seedbox.url);
		form.find('[name="username"]').val(seedbox.username);
		form.find('[name="password"]').val(seedbox.password);
		form.find('[name="label"]').val(seedbox.label);
		form.find('[name="webClient"]').val(seedbox.webClient);
		
		$('#modalseedbox').modal('show');
	});
});


function loadUsers() {
	$.getJSON('LoadUsers.vnt')
			.done(
					function(data) {
						var tbody = $('#userstable > tbody');
						tbody.html('');
						for (var i = 0; i < data.length; i++) {
							var user = data[i];
							var html = '';
							html += '<tr data-id="' + user.id + '">';
							html += '<td>' + user.username + '</td>';
							html += '<td>' + (user.admin?'Yes':'No') + '</td>';
							html += '<td>' + (user.sessions && user.sessions.length ? (user.sessions[0].session ? user.sessions[0].session : '-') : '-') + '</td>';
							html += '<td>' + (user.sessions && user.sessions.length ? (user.sessions[0].lastRequest ? user.sessions[0].lastRequest : '-') : '-') + '</td>';
							html += '<td>' + (user.sessions && user.sessions.length ? (user.sessions[0].lastIP ? user.sessions[0].lastIP : '-') : '-') + '</td>';
							html += '<td><i class="glyphicon glyphicon-remove" style="color:red;"></i></td>';
							html += '</tr>';
							tbody.append(html);
						}
					});
}

$('a[href="#users"]').on('show.bs.tab', function(e) {
	loadUsers();
})

$('#modaluser form').on('submit', function(e) {
	e.preventDefault();
	var form = $(this);
	$.post('SaveUser.vnt', form.serialize()).done(function(data) {
		if (data.success) {
			loadUsers();
			$('#modaluser').modal('hide');
		}
		else {
			alert('Problem when saving data');
			console.log(data.error);
		}
	});
	return false;
});

$('#userstable').on('click', 'tr td:last-child', function(e) {
	var tr = $(this).parents('tr');
	var pk = $(tr).data('id');
	$.ajax('SaveUser.vnt?id=' + pk, {
		method : 'DELETE'
	}).done(function(data) {
		if (data.success) {
			loadUsers();
		}
	});
});

$('#userstable').on('click', 'tr td:not(:last-child)', function(e) {
	var tr = $(this).parents('tr');
	var form = $('#modaluser form');
	var pk = $(tr).data('id');
	form.find('input[name="new"]').val('false');
	form.find('input[name="id"]').val(pk);
	$.getJSON('LoadUsers.vnt?id=' + pk).done(function(data){
		var user = data;
		
		form.find('[name="username"]').val(user.username);
		form.find('[name="password"]').val(user.password);
		form.find('[name="admin"]').prop('checked', user.admin);
		
		$('#modaluser').modal('show');
	});
});

function loadSchedules() {
	$.getJSON('LoadSchedulerDatas.vnt')
			.done(
					function(data) {
						var tbody = $('#schedulestable > tbody');
						tbody.html('');
						for (var i = 0; i < data.length; i++) {
							var schedule = data[i];
							var html = '';
							html += '<tr data-id="' + schedule.id + '">';
							html += '<td>' + schedule.name + '</td>';
							html += '<td>' + schedule.trackerUser.trackerName + ' - ' + schedule.trackerUser.username + '</td>';
							
							var service = '';
							if (schedule.serviceParser) {
								var sSplit = schedule.serviceParser.split('.');
								service = sSplit[sSplit.length - 1];
							}
							html += '<td>' + service + '</td>';
							html += '<td>' + schedule.email + '</td>';
							html += '<td>' + schedule.nextRun + '</td>';
							html += '<td><i class="glyphicon glyphicon-remove" style="color:red;"></i></td>';
							html += '</tr>';
							tbody.append(html);
						}
					});
	loadTotalSchedules();
}

$('a[href="#schedules"]').on('show.bs.tab', function(e) {
	loadSchedules();
})

$('#modalschedule form').on('submit', function(e) {
	e.preventDefault();
	var form = $(this);
	var formData = new FormData();
	$('#modalschedule form input[name], #modalschedule form select[name]').each(function(idx, obj) {
		if (obj.type != 'file') {
			formData.append(obj.name, obj.value);
		}
		else {
			formData.append(obj.name, obj.files[0]);
		}
	});
	$.ajax({
	    url: 'SaveScheduleDatas.vnt?' + form.serialize(),
	    data: formData,
	    cache: false,
	    contentType: false,
	    processData: false,
	    type: 'POST'
	}).done(function(data) {
		if (data.success) {
			loadSchedules();
			$('#modalschedule').modal('hide');
		}
		else {
			alert('Problem when saving data');
			console.log(data.error);
		}
	});
	return false;
});

$('#schedulestable').on('click', 'tr td:last-child', function(e) {
	var tr = $(this).parents('tr');
	var pk = $(tr).data('id');
	$.ajax('SaveScheduleDatas.vnt?id=' + pk , {
		method : 'DELETE'
	}).done(function(data) {
		if (data.success) {
			loadSchedules();
		}
	});
});

$('#schedulestable').on('click', 'tr td:not(:last-child)', function(e) {
	var tr = $(this).parents('tr');
	var form = $('#modalschedule form');
	var pk = $(tr).data('id');
	
	form.find('[name="tracker"]').removeAttr('readonly');
	form.find('input[name="new"]').val('false');
	form.find('input[name="id"]').val(pk);
	$.getJSON('LoadSchedulerDatas.vnt?id=' + pk).done(function(data){
		var schedule = data;
		
		form.find('[name="name"]').val(schedule.name);
		form.find('[name="tracker"]').val(schedule.trackerUser.tracker);
		loadTrackerUsersSelect(schedule.trackerUser.tracker).done(function() {
			form.find('[name="trackerUser"]').val(schedule.trackerUser.id);
		});
		form.find('[name="search"]').val(schedule.queryParameters.search);
		loadTrackerCategoriesSchedule(schedule.trackerUser.tracker).done(function() {
			var cats = [];
			for (var i=0; i<schedule.queryParameters.trackerCategories.length; i++) {
				cats.push(schedule.queryParameters.trackerCategories[i].code);
			}
			$("#trackercategoriesschedule").val(cats).trigger('change');
		});
		
		var divorigin = $('#div-torrent-filter-schedule');
		divorigin.find('table.table-torrent-filter > tbody').html('');
		var fields = [];
		for (var i=0; i<schedule.queryParameters.torrentFilters.length; i++) {
			var torrentFilter = schedule.queryParameters.torrentFilters[i];
			addTorrentFilter(torrentFilter.field, $('#torrentfieldsschedule option[value="' + torrentFilter.field + '"]').text(), divorigin);
			var filterschedule = $('#div-torrent-filter-schedule .table-torrent-filter > tbody > tr');
			var trSchedule = $(filterschedule[i]);
			trSchedule.find('[name="torrentfilteroperation"]').val(torrentFilter.operation);
			trSchedule.find('[name="torrentfiltervalue"]').val(torrentFilter.value);
			
		}
		if ($('#div-torrent-filter-schedule .table-torrent-filter').html() != '') {
			$('#div-torrent-filter-schedule').show();
		}
		
		form.find('[name="email"]').val(schedule.email);
		form.find('[name="startDate"]').val(schedule.nextRun);
		form.find('[name="interval"]').val(schedule.interval);
		
		$('#modalschedule').modal('show');
	});
	
	
});

$('#modalschedule form [name="tracker"]').on('change', function(e) {
	loadTrackerUsersSelect($(this).val());
	loadTrackerCategoriesSchedule($(this).val());
});


function loadTrackers() {
	$.getJSON('LoadTrackers.vnt').done(function(data) {
		var dropdowns = $('.dropdown-trackers');
		dropdowns.html('');
		dropdowns.append('<li data-tracker=""><a href="#">All</a></li>');
		var selectschedule = $('#modalschedule select[name="tracker"]');
		var selecttrackeruser = $('#modaltrackeruser select[name="tracker"]');
		selectschedule.html('');
		selecttrackeruser.html('');
		for (var i=0; i<data.length; i++) {
			var tracker = data[i];
			selecttrackeruser.append('<option value="' + tracker.clazz + '" data-hascaptcha="' + tracker.hasCaptcha + '">' + tracker.name + '</option>');
			if (tracker.avaliable) {
				selectschedule.append('<option value="' + tracker.clazz + '">' + tracker.name + '</option>');
				dropdowns.append('<li data-tracker="' + tracker.name + '" data-clazz="' + tracker.clazz + '"><a href="#">' + tracker.name + '</a></li>');
			}
			
		}
		dropdowns.append('<li role="separator" class="divider"></li>');
		dropdowns.append('<li><a href="#" class="advancedli">Advanced</a></li>');
	});
}

function loadServiceParsers() {
	$.getJSON('LoadServiceParsers.vnt').done(function(data) {
		var selects = $('select[name="serviceParser"]');
		selects.html('');
		for (var i=0; i<data.length; i++) {
			var serviceParsers = data[i];
			selects.append('<option value="' + serviceParsers.value + '">' + serviceParsers.name + '</option>');
		}
	});
}

function loadTrackerUsersSelect(tracker) {
	var jqxhr = $.getJSON('LoadTrackerUsers.vnt?tracker='+tracker).done(function(data) {
		var selects = $('select[name="trackerUser"]');
		selects.html('');
		for (var i=0; i<data.length; i++) {
			var trackeruser = data[i];
			selects.append('<option value="' + trackeruser.id + '">' + trackeruser.username + '</option>');
		}
	});
	return jqxhr;
}

function loadTotalSchedules() {
	$.getJSON('LoadSchedulerDatas.vnt?count=true').done(function(data) {
		var count = 0;
		if (data.success) {
			count = data.data;
		}
		$('a[href="#schedules"] > span').html(count);
	});
}

loadTrackers();
loadServiceParsers();
loadTotalSchedules();

$('.dropdown-trackers').on('click', 'li[data-tracker] > a', function(e) {
	e.preventDefault();
	var tracker = $(e.currentTarget).parent().data('tracker');
	var clazz = $(e.currentTarget).parent().data('clazz');
	var btnSelect = $(e.currentTarget).parent().parent().parent().find('button');
	btnSelect.html((tracker?tracker:'All') + ' <span class="caret"></span>');
	$('#tracker').val(clazz);
	loadTrackerCategories(clazz);
});

var trackersLoading = 0;

function verifyLoading() {
	if (trackersLoading > 0) {
		setTimeout(function() {
			verifyLoading();
		}, 100);
	}
	else {
		stopLoadingTorrents();
	}
}

$('#formfilter').on('submit', function(e) {
	e.preventDefault();
	var form = $(this);
	
	loadingTorrents();
	$('#divtrackerresults').html('');
	if ($('#tracker').val() == '') {
		var trackersAvaliable = $('.dropdown-trackers li[data-tracker]:not([data-tracker=""])');
		trackersAvaliable.each(function(idx, obj) {
			filter($(this).data('clazz'), form.serialize());
			trackersLoading++;
		});
		if (trackersAvaliable.length <= 0) {
			alert('Please add tracker users before you can search');
		}
	}
	else {
		filter($('#tracker').val(), form.serialize());
		trackersLoading++;
	}
	verifyLoading();
	
	return false;
});

function filter(tracker, formData) {
	loadSeedboxes();
	$.getJSON('SearchTrackers.vnt', formData + '&tracker=' + tracker).done(function(data) {
		if (data.success) {
			buildTrackerResultHtml(data.data);
			buildTrackerTorrentsResultHtml(data.data);
		}
		else {
			alert('Problem when loading data\n' + data.error.message);
			console.error(data.error.stack);
		}
	}).fail(function() {
		alert('Problem when loading data');
	}).always(function() {
		trackersLoading--;
	});
}

function buildTrackerResultHtml(data) {
	if ($('#divtracker-' + data.tracker).length > 0) {
			return;
	}
	var html = '';
	html += '<div class="col-xs-12" id="divtracker-' + data.tracker + '">';
	html += '<h4>' + data.tracker + ' <i style="cursor: pointer;" class="glyphicon glyphicon-time modalschedule" data-tracker="' + data.trackerClass + '" data-toggle="tooltip" data-placement="top" title="Create schedule with this filters"></i></h4>';
	html += '<div class="table-responsive table-responsive-tor">';
	html += '<table class="table table-striped table-hover table-results-tor">';
	html += '<thead>';
	html += '<tr>';
	html += '<th>#</th>';
	html += '<th class="col-xs-7">Name</th>';
	html += '<th class="col-xs-1">Size</th>';
	html += '<th class="col-xs-2">Category</th>';
	html += '<th class="col-xs-2">Added</th>';
	html += '<th style="width: 4%;"><i class="glyphicon glyphicon-cloud-upload"></i></th>';
	html += '<th style="width: 4%;"><i class="glyphicon glyphicon-cloud-download" style="color: #337ab7"></i></th>';
	html += '</tr>';
	html += '</thead>';
	html += '<tbody>';
	html += '</tbody>';
	html += '</table>';
	html += '</div>';
	html += '<nav>';
	html += '<ul class="pager pager-tor" data-page="0" data-tracker="' + data.tracker + '">';
	html += '<li class="previous"><a href="#"><span aria-hidden="true">&larr;</span> Prior</a></li>';
	html += '<li class="next"><a href="#">Next <span aria-hidden="true">&rarr;</span></a></li>';
	html += '</ul>';
	html += '</nav>';
	html += '</div>';
	$('#divtrackerresults').append(html);
}

function buildTrackerTorrentsResultHtml(data) {
	var torrents = data.torrents;
	var tracker = data.tracker;
	var divtracker = $('#divtracker-' + tracker);
	var tbodytorrents = divtracker.find('table.table-results-tor > tbody');
	tbodytorrents.html('');
	for (var i=0; i<torrents.length; i++) {
		var torrent = torrents[i];
		var html = '';
		html += '<tr data-torrent="' +  encodeURI(JSON.stringify(torrent)) + '">';
		html += '<th scope="row">' + torrent.id + '</th>';
		html += '<td>' + torrent.name + '&nbsp;&nbsp;<i class="fa fa-info-circle modaltorrentdetail" style="cursor: pointer; font-size: 18px;" title="Details"></i>&nbsp;&nbsp;&nbsp;&nbsp;<a href="' + torrent.link + '" target="_blank" title="Torrent tracker page"><i class="fa fa-external-link-square" style="color: #333; padding-bottom: 2px; font-size: 18px;" aria-hidden="true"></i></a></td>';
		html += '<td>' + readableFileSize(torrent.size) + '</td>';
		html += '<td>' + torrent.category + '</td>';
		html += '<td>' + (torrent.added?torrent.added:' - ') + '</td>';
		html += '<td><i class="glyphicon glyphicon-cloud-upload modalpickseedbox" style="cursor: pointer;" data-toggle="tooltip" data-placement="top" title="Send to Seedbox"></i></td>';
		html += '<td><a href="DownloadTorrent.vnt?torrent=' +  encodeURIComponent(JSON.stringify(torrent)) + '"><i class="glyphicon glyphicon-cloud-download" data-toggle="tooltip" data-placement="top" title="Download Torrent"></i></a></td>';
		html += '</tr>';
		tbodytorrents.append(html);
	} 
}

$('#divtrackerresults').on('click', 'div[id^="divtracker-"] > nav > ul.pager-tor > li > a', function(e) {
	e.preventDefault();
	var page = parseInt($(this).parent().parent().data('page'));
	if ($(this).parent().hasClass('next')) {
		page++;
	}
	else if (page > 0) {
		page--;
	}
	filter($(this).parent().parent().data('tracker'), $('#formfilter').serialize() + '&page=' + page);
	$(this).parent().parent().data('page', page);
});

$('.dropdown-trackers').on('click', '.advancedli', function(e) {
	e.preventDefault();
	$('#divadvanced').toggleClass('hide');
});

function loadingTorrents() {
	$('#advancedsubmit').button('loading');
	$('#iconfilterleft').addClass('fa fa-spinner fa-spin');
	$('#iconfilterleft').removeClass('glyphicon glyphicon-search');
}

function stopLoadingTorrents() {
	$('#advancedsubmit').button('reset');
	$('#iconfilterleft').addClass('glyphicon glyphicon-search');
	$('#iconfilterleft').removeClass('fa fa-spinner fa-spin');
}

function readableFileSize(size) {
    var units = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    var i = 0;
    while(size >= 1024) {
        size /= 1024;
        ++i;
    }
    return size.toFixed(1) + ' ' + units[i];
}

function loadTrackerCategories(tracker) {
	var jqxhr = $.getJSON('LoadTrackerCategories.vnt' + (tracker? '?tracker=' + tracker : '')).done(function(data) {
		var sel2Jq = $("#trackercategories");
		sel2Jq.html('');
		sel2Jq.select2();
		sel2Jq.select2('destroy');
		var dataTrackerCategories = [];
		for (var i=0; i<data.length; i++) {
			var trackerCategory = data[i];
			var item = {id: trackerCategory.trackerClass + '|' + trackerCategory.code, text:trackerCategory.name + (!tracker ? (' - ' + trackerCategory.tracker) : '') };
			dataTrackerCategories.push(item);
		}
		var select2 = $("#trackercategories").select2({
			  data: dataTrackerCategories
		});
		select2.val(null).trigger('change');
	});
	return jqxhr;
}

loadTrackerCategories();


var select2TorrentFields = $("#torrentfields").select2({
	allowClear: true
});

select2TorrentFields.on('select2:select', function(e) {
	$('#div-torrent-filter').show();
	var sel2jq = $(e.currentTarget);
	addTorrentFilter(sel2jq.val(), sel2jq.find('option:selected').text(), $('#div-torrent-filter'));
	sel2jq.val(null).trigger('change');
});

$('#div-torrent-filter').hide();

$('#div-torrent-filter').on('click', 'table > tbody > tr > td > i', function(e) {
	var tr = $(e.currentTarget).parents('tr');
	$(tr[0]).remove();
	if ($('#div-torrent-filter table > tbody > tr').length <= 0) {
		$('#div-torrent-filter').hide();		
	}
});

function addTorrentFilter(value, title, origin) {
	var html = '';
	html += '<tr>';
	html += '<td class="col-xs">' + title + ' <input type="hidden" name="torrentfiltername" value="' + value + '"></td>';
	html += '<td>';
	html += '<select id="torrentfilteroperation" name="torrentfilteroperation" class="form-control">';
	html += '<option value="">Operation</option>';
	html += '<option value="EQ">=</option>';
	html += '<option value="NE">!=</option>';
	html += '<option value="LT">&lt;</option>';
	html += '<option value="GT">&gt;</option>';
	html += '<option value="LE">&lt;=</option>';
	html += '<option value="GE">&gt;=</option>';
	html += '<option value="LIKE">Like</option>';
	html += '<option value="NLIKE">Not Like</option>';
	html += '<option value="REGEX">Regex</option>';
	html += '</select>';
	html += '</td>';
	html += '<td><input type="text" name="torrentfiltervalue" class="form-control" placeholder="Value"></td>';
	html += '<td><i class="glyphicon glyphicon-remove" style="color: red;"></i></td>';
	html += '</tr>';
	origin.find('.table-torrent-filter').append(html);
}

function loadTrackerCategoriesSchedule(tracker) {
	var sel2Jq = $("#trackercategoriesschedule");
	sel2Jq.html('');
	sel2Jq.select2();
	sel2Jq.select2('destroy');
	var dataTrackerCategories = [];
	var jqxhr = $.getJSON('LoadTrackerCategories.vnt' + (tracker? '?tracker=' + tracker : '')).done(function(data) {
		for (var i=0; i<data.length; i++) {
			var trackerCategory = data[i];
			var item = {id: trackerCategory.code, text:trackerCategory.name + (!tracker ? (' - ' + trackerCategory.tracker) : '') };
			dataTrackerCategories.push(item);
		}
		var select2 = sel2Jq.select2({
			  data: dataTrackerCategories
		});
		sel2Jq.val(null).trigger('change');
	});
	return jqxhr;
}

var select2TorrentFieldsSchedule = $("#torrentfieldsschedule").select2({
	allowClear: true
});

select2TorrentFieldsSchedule.on('select2:select', function(e) {
	$('#div-torrent-filter-schedule').show();
	var sel2jq = $(e.currentTarget);
	addTorrentFilter(sel2jq.val(), sel2jq.find('option:selected').text(), $('#div-torrent-filter-schedule'));
	sel2jq.val(null).trigger('change');
});

$('#div-torrent-filter-schedule').hide();

$('#div-torrent-filter-schedule').on('click', 'table > tbody > tr > td > i', function(e) {
	var tr = $(e.currentTarget).parents('tr');
	$(tr[0]).remove();
	if ($('#div-torrent-filter-schedule table > tbody > tr').length <= 0) {
		$('#div-torrent-filter-schedule').hide();		
	}
});

$('#modalmailconfig').on('show.bs.modal', function(e) {
	var form = $('#modalmailconfig form');
	$.getJSON('Settings.vnt?configuration=mailConfig').done(function(data) {
		if (data.success) {
			form.find('[name="hostname"]').val(data.data.hostname);
			form.find('[name="port"]').val(data.data.port);
			form.find('[name="ssl"]').val(data.data.ssl + '');
			form.find('[name="tls"]').val(data.data.tls + '');
			form.find('[name="username"]').val(data.data.username);
			form.find('[name="password"]').val(data.data.password);
			form.find('[name="from"]').val(data.data.from);
			form.find('[name="fromName"]').val(data.data.fromName);
		}
	});
});

$('#modalmailconfig form').on('submit', function(e) {
	e.preventDefault();
	var form = $(this);
	$.post('Settings.vnt?configuration=mailConfig', form.serialize()).done(function(data) {
		if (data.success) {
			$('#modalmailconfig').modal('hide');			
		}
		else {
			alert('Problem when saving data');
		}
	}).fail(function() {
		alert('Problem when saving data');
	});
	return false;
});

$('#modalgeneralconfig').on('show.bs.modal', function(e) {
	var form = $('#modalgeneralconfig form');
	$.getJSON('Settings.vnt?configuration=generalConfig').done(function(data) {
		if (data.success) {
			form.find('[name="baseUrl"]').val(data.data.baseUrl);
			form.find('[name="maxSessionsPerUser"]').val(data.data.maxSessionsPerUser);
			form.find('[name="imdbActive"]').prop('checked', data.data.imdbActive);
			form.find('[name="tmdbActive"]').prop('checked', data.data.tmdbActive).trigger('change');
			form.find('[name="tmdbapikey"]').val(data.data.tmdbapikey);
			form.find('[name="tmdbLanguage"]').val(data.data.tmdbLanguage).trigger('change');
			
		}
	});
});

$('#modalgeneralconfig form [name="tmdbActive"]').on('change', function(e) {
	if ($(e.currentTarget).prop('checked')) {
		$('#tmdbapikeydiv').show();
	}
	else {
		$('#tmdbapikeydiv').hide();
	}
});

$('#modalgeneralconfig form').on('submit', function(e) {
	e.preventDefault();
	var form = $(this);
	$.post('Settings.vnt?configuration=generalConfig', form.serialize()).done(function(data) {
		if (data.success) {
			$('#modalgeneralconfig').modal('hide');			
		}
		else {
			alert('Problem when saving data');
		}
	}).fail(function() {
		alert('Problem when saving data');
	});
	return false;
});

$('[name="tmdbLanguage"]').select2({
	allowClear : true
});

function loadUserData() {
	$.getJSON('LoadUserData.vnt').done(function(data) {
		if (data && data.success) {
			if (data.data.admin) {
				$('#liusers').removeClass('hide');
				$('#lisettings').removeClass('hide');
			}
			var liuserdata = $('#liuserdata');
			liuserdata.find('> a').html(data.data.user + ' <span class="caret"></span>');
			liuserdata.removeClass('hide');
		}
	});
}

loadUserData();