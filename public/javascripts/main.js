function displayResult(data) {

    let resp = JSON.parse(data);
    let projects = resp["projects"];
    let newSection = $('<div class="centeredBlock"/>');

    if (Object.keys(projects).length > 0) {

        let resultTable = $('<table class="projectTable"><thead><tr><th>Owner</th><th>Submit date</th><th>Title</th><th>Skills</th><th>Word statistics</th></tr></thead>');

        for (let id of Object.keys(projects)) {

            let project = projects[id];

            // Create a row for a project
            let row = $('<tr/>');
            row.append($('<td/>').append($('<a href="/employer/' + project.owner_id + '">' + project.owner_id + '</a>')));
            row.append($('<td/>').append(project.submitdate));
            row.append($('<td/ class="projectTitle">').append(project.title));

            let skillsContainer = $('<table class="centeredBlock skillsTable"/>');
            let ref = project.skills;
            for (let i = 0; i < ref.length; i++) {
                let skill = ref[i];
                skillsContainer.append($('<tr/>').append($('<td/>').append($('<a href="/skill/' + skill.id + '" target="_blank">' + skill.name + '</a>'))));
            }

            row.append($('<td/>').append(skillsContainer));
            row.append($('<td/>').append($('<a href="/stats/' + id + '">View stats</a>')));
            row.append($('<td/>').append($('<a href="/readability/' + encodeURIComponent(project["preview_description"]) + '" target="_blank">' + 'Readability' + '</a>')));

            resultTable.append(row);
        }
        resultTable.append($('</table>'));

        // Create the new section that will display the search results
        newSection.append($('<h3>Search results for keywords <a href="/searchstats/' + encodeURI(resp.keywords) + '">' + resp.keywords + '</a></h3>'));
        if (resp["flesch_index"] != null) {
            newSection.append($('<h4>Flesh Reading Ease Index ' + resp["flesch_index"] + ' & FKGL ' + resp["FKGL"] + '</h4>'));
        }
        newSection.append(resultTable);
    } else {
        newSection.append($('<h3>No project was found using keywords "' + resp.keywords + '".</h3>'));
    }
    return newSection;
}
