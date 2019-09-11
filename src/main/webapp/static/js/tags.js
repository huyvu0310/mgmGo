class AutoCompleteTagsSource {
    constructor(tagBox) {
        this.fetch = this.fetch.bind(this);
        this.createExtraItem = this.createExtraItem.bind(this);
        this.createInput = this.createInput.bind(this);

        this.tagBox = tagBox;
    }

    fetch(receiver, ac) {
        this.request = $.get("/api/tag", {
            content: ac.getValue(),
            listAvailableContent: this.tagBox.getValues()
        }, function (data) {
            receiver(data, data.indexOf(ac.getValue()));
        });
    }

    cancel() {
        if (this.request) this.request.abort();
    }

    createInput() {
        var input = AutoComplete.createInput();
        input.addEventListener("keydown beforeinput", (e) => {
            var currentInput = $(e.target);
            if (this.filterValue(currentInput.val()).length >= AutoCompleteTagsSource.MAX_INPUT_LENGTH) {
                e.preventDefault();
                e.stopImmediatePropagation();
            }
        });
        input.addEventListener("input", (e) => {
            var currentInput = $(e.target);
            if (currentInput.val().length >= AutoCompleteTagsSource.MAX_INPUT_LENGTH) {
                if (this.inputTimeout) clearTimeout(this.inputTimeout);
                this.inputTimeout = setTimeout(function () {
                    currentInput.val(currentInput.val().substr(0, AutoCompleteTagsSource.MAX_INPUT_LENGTH));
                }, 1);
                e.stopPropagation();
            }
        });
        return input;
    }

    filterValue(val) {
        return val.trim().replace(/[ ]+/g, ' ');
    }

    getValueFromData(data) {
        return data.content;
    }

    createItem(value) {
        var li = document.createElement("li");
        li.innerText = value.content;
        return li;
    }

    createExtraItem(value) {
        if (this.tagBox.disableCreate) return null;
        if (this.containsValue(value)) return null;
        return AutoComplete.createExtraItem(value);
    }

    containsValue(value) {
        var contains = false;
        this.tagBox.getValues().forEach((tag) => {
            if (this.compareValue(tag, value)) {
                contains = true;
                return;
            }
        });
        return contains;
    }

    compareValue(val1, val2) {
        return val1.toLowerCase() === val2.toLowerCase();
    }
}

AutoCompleteTagsSource.MAX_INPUT_LENGTH = 20;


class TagBox {
    constructor(selector, maxTags = 3, disableCreate = false) {
        this.tagBox = $(selector);

        this.disableCreate = disableCreate;

        this.ac = new AutoComplete(this.tagBox.find(".auto-complete")[0],
            new AutoCompleteTagsSource(this), 500, undefined, undefined, AutoCompleteTagsSource.autoCompleteCreateItem);

        this.inputBox = $(this.tagBox.find("." + TagBox.INPUT_CLASS)[0]);
        this.input = $(this.ac.input);
        this.placeholder = this.tagBox.attr("placeholder") ? this.tagBox.attr("placeholder") : "";

        this.elementsList = [];

        this.maxTags = maxTags;

        this.handleTagSelect = this.handleTagSelect.bind(this);
        this.handleBoxClick = this.handleBoxClick.bind(this);
        this.handleInputFocusOut = this.handleInputFocusOut.bind(this);
        this.handleElementClick = this.handleElementClick.bind(this);
        this.handleInputKeydown = this.handleInputKeydown.bind(this);

        this.ac.onSelect(this.handleTagSelect);
        this.tagBox.click(this.handleBoxClick);
        this.inputBox.focusout(this.handleInputFocusOut);
        this.inputBox.keydown(this.handleInputKeydown);

        $("." + TagBox.ELEMENT_CLASS).click(this.handleElementClick);

        this.checkMaxTags();

        this.loadDefaultTags();
    }

    loadDefaultTags() {
        var elements = this.findAllElement();
        for (var i = 0; i < elements.length; i++) {
            var element = $(elements[i]);
            this.elementsList.push(this.getDataFromElement(element));
        }
        if (!element || element.length == 0) {
            this.input.attr("placeholder", this.placeholder);
        }
    }

    handleBoxClick() {
        if (this.isDisabled()) return;
        this.tagBox.addClass(TagBox.FOCUSED_CLASS);
        this.focusInput();
    }

    handleTagSelect(v) {
        this.addTag(v);
        this.input.val("");
    }

    handleInputFocusOut() {
        this.tagBox.removeClass(TagBox.FOCUSED_CLASS);
    }

    handleInputKeydown(e) {
        if (e.keyCode == 13) {
            e.preventDefault();
        }
    }

    addTag(data) {
        var v = null, id = null;
        if (data == null) {
            v = this.ac.getValue();
            id = -1;
        } else {
            v = data.content;
            id = data.id;
        }
        if (this.containValue(v)) return;

        var element = this.generateElement(id, v);

        element.insertBefore(this.inputBox);

        this.elementsList.push({id: id, content: v});
        this.reloadInputs();

        this.focusInput();
        this.checkMaxTags();
    }

    reloadInputs() {
        this.findAllElementInputs().remove();
        for (var i = 0; i < this.elementsList.length; i++) {
            var data = this.elementsList[i];
            this.addInputs(data.id, data.content, i);
        }
        if (this.elementsList.length > 0) {
            this.input.attr("placeholder", "");
        } else {
            this.input.attr("placeholder", this.placeholder);
        }
    }

    generateElement(id, v) {
        var element = $("<div>", {class: TagBox.ELEMENT_CLASS, text: v});
        element.click(this.handleElementClick);
        return element;
    }

    addInputs(id, v, position) {
        this.tagBox.append(this.generateInput(v, this.getIdInputName(position), id, id, TagBox.ELEMENT_ID_INPUT_CLASS));
        this.tagBox.append(this.generateInput(v, this.getContentInputName(position), id, v, TagBox.ELEMENT_CONTENT_INPUT_CLASS));
        this.count++;
    }

    generateInput(element, name, id, value, subClass = "") {
        var input = $("<input>", {
            class: TagBox.ELEMENT_INPUT_CLASS + " " + subClass,
            type: "hidden",
            name: name,
            value: value
        });
        return input;
    }

    getValues() {
        var list = this.getAllValueInputs();
        var res = [];
        for (var i = 0; i < list.length; i++) {
            res.push(list[i].value);
        }
        return res;
    }

    checkMaxTags() {
        var maxed = this.maxTags > 0 && this.getTagsAmount() >= this.maxTags;
        if (maxed) {
            this.disableBox();
        } else if (this.input.prop("disabled")) {
            this.enableBox();
        }
        return maxed;
    }

    disableBox() {
        this.tagBox.addClass(TagBox.DISABLED_CLASS);
        this.tagBox.removeClass(TagBox.FOCUSED_CLASS);
        this.input.prop("disabled", true);
        this.input.addClass("d-none");
        this.input.removeClass("d-block");
    }

    isDisabled() {
        return this.tagBox.hasClass(TagBox.DISABLED_CLASS);
    }

    enableBox() {
        this.tagBox.removeClass(TagBox.DISABLED_CLASS);
        this.input.prop("disabled", false);
        this.input.addClass("d-block");
        this.input.removeClass("d-none");
    }

    getTagsAmount() {
        return this.tagBox.find("." + TagBox.ELEMENT_CLASS).length;
    }

    handleElementClick(e) {
        var element = $(e.target);
        this.removeElement(element);
    }

    removeElementFromList(element) {
        for (var i = 0; i < this.elementsList.length; i++) {
            if (this.elementsList[i].content === this.getElementValue(element)) {
                this.elementsList.splice(i, 1);
                return;
            }
        }
    }

    removeElement(element) {
        element.remove();
        this.removeElementFromList(element);
        this.reloadInputs();
        this.checkMaxTags();
    }

    getDataFromElement(element) {
        var contentInput = this.findContentInput(element);
        var index = this.findInputIndex(contentInput);
        return {id: this.findIdInputByIndex(index).val(), content: contentInput.val()};
    }

    findIdInputByIndex(index) {
        return $(this.tagBox.find("." + TagBox.ELEMENT_ID_INPUT_CLASS + '[name="' + this.getIdInputName(index) + '"]')[0]);
    }

    getIdInputName(index) {
        return TagBox.ID_INPUT_NAME.replace("{}", index);
    }

    getContentInputName(index) {
        return TagBox.CONTENT_INPUT_NAME.replace("{}", index);
    }

    findInputIndex(input) {
        var name = input.attr("name");
        var leftPos = name.indexOf("[") + 1;
        var rightPos = name.indexOf("]");
        return name.substr(leftPos, rightPos - leftPos);
    }

    findContentInput(element) {
        return $(this.tagBox.find("." + TagBox.ELEMENT_CONTENT_INPUT_CLASS + '[value="' + this.getElementValue(element) + '"]')[0]);
    }

    getElementValue(element) {
        return element.html();
    }

    containValue(content) {
        for (var i = 0; i < this.elementsList.length; i++) {
            if (this.elementsList[i].content.toLowerCase() === content.toLowerCase()) return true;
        }
        return false;
    }

    findAllElementInputs() {
        return this.tagBox.find("." + TagBox.ELEMENT_INPUT_CLASS);
    }

    findAllElement() {
        return this.tagBox.find("." + TagBox.ELEMENT_CLASS);
    }

    getAllValueInputs() {
        return this.tagBox.find("." + TagBox.ELEMENT_CONTENT_INPUT_CLASS);
    }

    focusInput() {
        this.input.focus();
    }
}

TagBox.ELEMENT_INPUT_CLASS = "tag-box-value";
TagBox.ELEMENT_CONTENT_INPUT_CLASS = "tag-box-value-content";
TagBox.ELEMENT_ID_INPUT_CLASS = "tag-box-value-id";
TagBox.ELEMENT_CLASS = "tag-box-element";
TagBox.INPUT_CLASS = "tag-box-input";
TagBox.FOCUSED_CLASS = "tag-box-focused";
TagBox.DISABLED_CLASS = "tag-box-disabled";
TagBox.CONTENT_INPUT_NAME = "tags[{}].content";
TagBox.ID_INPUT_NAME = "tags[{}].id";