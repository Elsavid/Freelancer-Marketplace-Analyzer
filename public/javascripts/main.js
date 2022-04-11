function displayResult(data) {

    let response = JSON.parse(data);
    console.log(response);
    let newDiv = $('<div class="centeredBlock"/>');

    for (let key of Object.keys(response)) {
        newDiv.prepend(generateProjectsTable(response[key]));
    }
    return newDiv;
}

function generateProjectsTable(projectsJson) {

    let keywords = projectsJson.keywords;
    let projects = projectsJson["projects"];
    let newSection = $('<div class="centeredBlock"/>');

    if (Object.keys(projects).length > 0) {

        let resultTable = $('<table class="projectTable"><thead><tr><th>Owner</th><th>Submit date</th><th>Title</th><th>Skills</th><th>Word statistics</th></tr></thead>');

        for (let id of Object.keys(projects)) {
            resultTable.append(generateProjectRow(id, projects[id]));
        }
        resultTable.append($('</table>'));

        // Create the new section that will display the search results
        newSection.append($('<h3>Search results for keywords <a href="/searchstats/' + encodeURI(keywords) + '" target="_blank">' + keywords + '</a></h3>'));
        newSection.append($('<h4>Flesch Reading Ease Index ' + projectsJson["flesch_index"] + ' & FKGL ' + projectsJson["FKGL"] + '</h4>'));
        newSection.append(resultTable);
      
    } else {
        newSection.append($('<h3>No project was found using keywords "' + keywords + '".</h3>'));
    }
    return newSection;
}

function generateProjectRow(id, project) {
    // Create a row for a project
    let row = $('<tr/>');
    row.append($('<td/>').append($('<a href="/employer/' + project.owner_id + '" target="_blank">' + project.owner_id + '</a>')));
    row.append($('<td/>').append(project.submitdate));
    row.append($('<td/ class="projectTitle">').append(project.title));

    let skillsContainer = $('<table class="centeredBlock skillsTable"/>');
    let ref = project.skills;
    for (let i = 0; i < ref.length; i++) {
        let skill = ref[i];
        skillsContainer.append($('<tr/>').append($('<td/>').append($('<a href="/skill/' + skill.id + '" target="_blank">' + skill.name + '</a>'))));
    }

    row.append($('<td/>').append(skillsContainer));
    row.append($('<td/>').append($('<a href="/stats/' + id + '" target="_blank">View stats</a>')));
    row.append($('<td/>').append($('<a href="/readability/' + id + '" target="_blank">' + 'Readability' + '</a>')));

    return row;
}
