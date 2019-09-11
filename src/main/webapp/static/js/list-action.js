$(document).ready(function () {
    $("#collapseFilter").on("show.bs.collapse", function (e) {
        $(".filter-show-btn").addClass("filter-show-btn-on");
    });
    $("#collapseFilter").on("hidden.bs.collapse", function (e) {
        $(".filter-show-btn").removeClass("filter-show-btn-on");
    });
    $(".sort-select").on("input", handleSortSelect);

    if ($(".filter-tag-box")) {
        $(".filter-apply-btn").click(handleFilterApply);

        var FILTER_TAG_PARAM = "filterTags=";
        var SORT_TAG_PARAM = "sortType=";

        var tagBox = new TagBox(".filter-tag-box", -1, true);

        loadDefaultFilter();
    }

    function handleSortSelect(e) {
        var type = $(e.target).val();
        document.location.href = addFilterParams(document.location.href, [type], SORT_TAG_PARAM);
    }

    function handleFilterApply(e) {
        var tags = tagBox.getValues();

        document.location.href = addFilterParams(document.location.href, tags, FILTER_TAG_PARAM);
    }

    function loadDefaultFilter() {
        var link = window.location.href;
        if (link.indexOf("?") < 0) return;
        var query = link.substr(link.indexOf("?") + 1);
        var params = query.split("&");
        params.forEach((param) => {
            if (param.startsWith(FILTER_TAG_PARAM)) {
                tagBox.addTag({id: null, content: param.substr(FILTER_TAG_PARAM.length)});
            }
        });
    }

    function addFilterParams(link, tags, key) {
        if (link.indexOf("?") < 0) {
            return link + "?" + buildFilterParams(tags, key);
        }
        link = removeOldFilterParams(link, key);

        var params = buildFilterParams(tags, key);
        var deli = "?";
        if (params.length == 0) deli = "";
        else if (link.indexOf("?") >= 0 && link.indexOf("?") + 1 < link.length) deli = "&";
        return link + deli + params;
    }

    function removeOldFilterParams(link, key) {
        var query = link.substr(link.indexOf("?") + 1);
        var params = query.split("&");
        params.forEach((param) => {
            if (param.startsWith(key)) {
                link = link.replace(param, "");
            }
        });
        link = link.replace(/[&]+/g, "&");
        if (link.endsWith("&")) link = link.substr(0, link.length - 1);
        return link.length - link.indexOf("?") - 1 > 2 ? link : link.substr(0, link.indexOf("?"));
    }

    function buildFilterParams(tags, key) {
        var params = [];
        tags.forEach((tags) => {
            params.push(key + tags);
        });
        return params.join("&");
    }
});
