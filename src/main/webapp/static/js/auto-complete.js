class AutoComplete {
    constructor(selector, source, delay = 500) {

        if (!(typeof source.fetch === "function") || source.fetch.length == 0) {
            throw "'source' is not valid. No 'fetch' method found."
        }

        this.source = source;
        this.setupCreators();

        this.initContent(selector);

        this.delay = delay;
        this.onSelectCallbacks = [];
        this.valuesList = [];
        this.selectedIndex = 0;

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleInputKeyPress = this.handleInputKeyPress.bind(this);
        this.handleInputBlur = this.handleInputBlur.bind(this);
        this.handleInputFocus = this.handleInputFocus.bind(this);
        this.search = this.search.bind(this);
        this.receiveData = this.receiveData.bind(this);

        this.input.addEventListener("input", this.handleInputChange);
        this.input.addEventListener("keydown", this.handleInputKeyPress);
        this.input.addEventListener("focus", this.handleInputFocus);
        this.input.addEventListener("blur", this.handleInputBlur);
    }

    initContent(selector) {
        this.wrapper = typeof selector === 'string' ? document.querySelector(selector) : selector;
        AutoComplete.emptyContainer(this.wrapper);
        this.wrapper.classList.add(AutoComplete.WRAPPER_CLASS);

        this.input = this.createInput();
        this.input.classList.add(AutoComplete.INPUT_CLASS);
        this.wrapper.appendChild(this.input);

        this.list = this.createList();
        this.list.classList.add(AutoComplete.LIST_CLASS);
        this.wrapper.appendChild(this.list);
    }

    handleInputBlur() {
        this.hideList();
    }

    handleInputFocus() {
        if (this.list.childNodes.length > 0) {
            this.showList();
        }
    }

    handleInputChange() {
        if (this.getValue().length === 0) {
            this.clean();
        } else {
            this.initSearch();
        }
    }

    handleInputKeyPress(e) {
        if (this.isListHidden()) return;
        var preventDefault = true;
        switch (e.keyCode) {
            case 40:
                this.down();
                break;
            case 38:
                this.up();
                break;
            case 13:
                this.select();
                break;
            default:
                preventDefault = false;
                break;
        }
        if (preventDefault) {
            e.preventDefault();
            e.stopImmediatePropagation();
        }
    }

    up() {
        this.selectItem(this.selectedIndex - 1);
    }

    down() {
        this.selectItem(this.selectedIndex + 1);
    }

    select() {
        var result = this.getSelectedValue();

        this.updateInput(result);

        this.onSelectCallbacks.forEach(function (callback) {
            callback(result);
        });

        this.clean();

        setTimeout(() => {
            this.input.focus()
        }, 10);
    }

    updateInput(data) {
        if (!data) {
            return;
        }

        if (typeof this.source.getTextFromObj === "function") {
            data = this.source.getTextFromObj(data);
        }

        this.setVal(data);

    }

    setVal(text) {
        this.input.value = text;
    }

    selectItem(index) {
        if (this.isListHidden()) return;

        this.unselectItems();

        var items = this.list.querySelectorAll("." + AutoComplete.ITEM_CLASS);
        index = index < 0 ? items.length - 1 : (index >= items.length ? 0 : index);

        items[index].classList.add(AutoComplete.SELECTED_CLASS);
        this.selectedIndex = index;
    }

    getSelectedValue() {
        return this.getSelectedItem().classList.contains(AutoComplete.EXTRA_CLASS) ? null : this.valuesList[this.selectedIndex];
    }

    onSelect(callback) {
        if (this.onSelectCallbacks.indexOf(callback) < 0) {
            this.onSelectCallbacks.push(callback);
        }
    }

    hideList() {
        this.list.style.display = "none";
    }

    showList() {
        if (this.isInputFocused()) {
            this.list.style.display = "block";
        }
    }

    isInputFocused() {
        return document.activeElement == this.input;
    }

    clean() {
        this.cleanUpLastSearch();
        this.hideList();
        this.emptyList();
        this.selectedIndex = 0;
    }

    cleanUpLastSearch() {
        clearTimeout(this.timeout);
        if (typeof this.source.cancel === "function") this.source.cancel();
    }

    initSearch() {
        this.cleanUpLastSearch();
        this.timeout = setTimeout(this.search, this.delay);
    }

    search() {
        this.hideList();
        if (this.source.fetch.length >= 2) {
            this.source.fetch(this.receiveData, this);
        } else this.source.fetch(this.receiveData);
    }

    isListHidden() {
        return this.list.childNodes.length === 0 || this.list.style.display === "none";
    }

    unselectItems() {
        var item = this.getSelectedItem();
        if (item) item.classList.remove(AutoComplete.SELECTED_CLASS);
    }

    getSelectedItem() {
        return this.list.querySelector("." + AutoComplete.SELECTED_CLASS);
    }

    receiveData(values, showExtra = true) {
        this.fillList(values, showExtra);
    }

    emptyList() {
        AutoComplete.emptyContainer(this.list);
        this.emptyValuesList();
    }

    fillList(values, showExtra) {
        this.emptyList();

        var contains = false;
        var currentValue = this.getValue();

        for (var i = 0; i < values.length; i++) {
            this.list.appendChild(this.generateItem(values[i], i));
            this.valuesList.push(values[i]);
            if (this.compareValue(this.getValueFromData(values[i]), currentValue)) {
                contains = true;
            }
        }
        if (showExtra && !contains) {
            var item = this.generateExtraItem();
            if (item) this.list.appendChild(item);
            else showExtra = false;
        }
        if (values.length == 0 && !showExtra) {
            return;
        }
        this.showList();
        this.selectItem(0);
    }

    getValue() {
        return this.filterValue(this.input.value);
    }

    emptyValuesList() {
        this.valuesList.splice(0, this.valuesList.length);
    }

    generateItem(value, index) {
        var item = this.createItem(value);
        item.classList.add(AutoComplete.ITEM_CLASS);
        item.addEventListener("mouseover", () => {
            this.selectItem(index);
        });
        item.addEventListener("mousedown", () => {
            this.selectItem(index);
            this.select();
        });
        return item;
    }

    generateExtraItem() {
        var value = this.getValue();
        if (value.length === 0) return null;
        var item = this.createExtraItem(value);
        if (!item) return item;
        item.classList.add(AutoComplete.ITEM_CLASS);
        item.classList.add(AutoComplete.EXTRA_CLASS);

        item.addEventListener("mouseover", () => {
            this.selectItem(this.valuesList.length);
        });
        item.addEventListener("mousedown", () => {
            this.selectItem(this.valuesList.length);
            this.select();
        });

        return item;
    }

    static createItem(value) {
        var li = document.createElement("li");
        li.innerText = value;
        return li;
    }

    static createExtraItem(value) {
        return AutoComplete.createItem(`Create "${value}" tag`);
    }

    static createInput() {
        var input = document.createElement("input");
        input.type = "text";
        input.setAttribute("autocomplete", "off");
        return input;
    }

    static createList() {
        return document.createElement("ul");
    }

    static emptyContainer(container) {
        if (container) {
            while (container.firstChild) container.removeChild(container.firstChild);
        }
    }

    static filterValue(val) {
        return val.trim();
    }

    static compareValue(val1, val2) {
        return val1 === val2;
    }

    static getValueFromData(data) {
        return data;
    }

    setupCreators() {
        this.addCreator("createInput");
        this.addCreator("createList");
        this.addCreator("createItem");
        this.addCreator("createExtraItem");
        this.addCreator("filterValue");
        this.addCreator("compareValue");
        this.addCreator("getValueFromData");
    }

    addCreator(name) {
        var func = this.source[name];
        if (typeof func !== "undefined") {
            this[name] = func;
        } else {
            this[name] = AutoComplete[name];
        }
    }
}

AutoComplete.WRAPPER_CLASS = "auto-complete";
AutoComplete.INPUT_CLASS = "auto-complete-input";
AutoComplete.LIST_CLASS = "auto-complete-list";
AutoComplete.ITEM_CLASS = "auto-complete-item";
AutoComplete.SELECTED_CLASS = "auto-complete-selected";
AutoComplete.EXTRA_CLASS = "auto-complete-create";
