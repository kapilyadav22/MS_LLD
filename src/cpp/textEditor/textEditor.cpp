/*
Design Overview:

Core Components
    Document → Holds content
    Command Pattern → Undo/Redo support
    Editor → Orchestrator
    TextStyle → Formatting (bold, italic)
    CommandManager → Undo/Redo stacks

Key Design Patterns Used
    Command Pattern → Undo/Redo
    Single Responsibility
    Encapsulation

*/

#include <bits/stdc++.h>
using namespace std;

/* ===================== TEXT STYLE ===================== */
class TextStyle {
public:
    bool bold = false;
    bool italic = false;

    string apply(const string &text) {
        string res = text;
        if (bold) res = "<b>" + res + "</b>";
        if (italic) res = "<i>" + res + "</i>";
        return res;
    }
};

/* ===================== DOCUMENT ===================== */
class Document {
private:
    string content;

public:
    void insert(int pos, const string &text) {
        content.insert(pos, text);
    }

    void erase(int pos, int len) {
        content.erase(pos, len);
    }

    string getText() const {
        return content;
    }
};

/* ===================== COMMAND ===================== */
class Command {
public:
    virtual void execute() = 0;
    virtual void undo() = 0;
    virtual ~Command() = default;
};

/* ===================== INSERT COMMAND ===================== */
class InsertCommand : public Command {
private:
    shared_ptr<Document> doc;
    int pos;
    string text;

public:
    InsertCommand(shared_ptr<Document> d, int p, string t)
        : doc(d), pos(p), text(t) {}

    void execute() override {
        doc->insert(pos, text);
    }

    void undo() override {
        doc->erase(pos, text.size());
    }
};

/* ===================== DELETE COMMAND ===================== */
class DeleteCommand : public Command {
private:
    shared_ptr<Document> doc;
    int pos, len;
    string deletedText;

public:
    DeleteCommand(shared_ptr<Document> d, int p, int l)
        : doc(d), pos(p), len(l) {}

    void execute() override {
        deletedText = doc->getText().substr(pos, len);
        doc->erase(pos, len);
    }

    void undo() override {
        doc->insert(pos, deletedText);
    }
};

/* ===================== FORMAT COMMAND ===================== */
class FormatCommand : public Command {
private:
    shared_ptr<Document> doc;
    int pos, len;
    TextStyle style;
    string oldText;

public:
    FormatCommand(shared_ptr<Document> d, int p, int l, TextStyle s)
        : doc(d), pos(p), len(l), style(s) {}

    void execute() override {
        oldText = doc->getText().substr(pos, len);
        string formatted = style.apply(oldText);
        doc->erase(pos, len);
        doc->insert(pos, formatted);
    }

    void undo() override {
        doc->erase(pos, style.apply(oldText).size());
        doc->insert(pos, oldText);
    }
};

/* ===================== COMMAND MANAGER ===================== */
class CommandManager {
private:
    stack<shared_ptr<Command>> undoStack;
    stack<shared_ptr<Command>> redoStack;

public:
    void executeCommand(shared_ptr<Command> cmd) {
        cmd->execute();
        undoStack.push(cmd);
        while (!redoStack.empty()) redoStack.pop();
    }

    void undo() {
        if (undoStack.empty()) return;
        auto cmd = undoStack.top();
        undoStack.pop();
        cmd->undo();
        redoStack.push(cmd);
    }

    void redo() {
        if (redoStack.empty()) return;
        auto cmd = redoStack.top();
        redoStack.pop();
        cmd->execute();
        undoStack.push(cmd);
    }
};

/* ===================== EDITOR ===================== */
class Editor {
private:
    shared_ptr<Document> doc;
    CommandManager manager;

public:
    Editor() {
        doc = make_shared<Document>();
    }

    void insert(int pos, string text) {
        manager.executeCommand(make_shared<InsertCommand>(doc, pos, text));
    }

    void erase(int pos, int len) {
        manager.executeCommand(make_shared<DeleteCommand>(doc, pos, len));
    }

    void format(int pos, int len, bool bold, bool italic) {
        TextStyle style;
        style.bold = bold;
        style.italic = italic;

        manager.executeCommand(
            make_shared<FormatCommand>(doc, pos, len, style)
        );
    }

    void undo() {
        manager.undo();
    }

    void redo() {
        manager.redo();
    }

    void print() {
        cout << doc->getText() << endl;
    }
};

/* ===================== MAIN ===================== */
int main() {

    Editor editor;
    int q;
    cin >> q;

    while (q--) {
        string cmd;
        cin >> cmd;

        if (cmd == "INSERT") {
            int pos;
            string text;
            cin >> pos;
            cin.ignore();
            getline(cin, text);
            editor.insert(pos, text);
        }
        else if (cmd == "DELETE") {
            int pos, len;
            cin >> pos >> len;
            editor.erase(pos, len);
        }
        else if (cmd == "FORMAT") {
            int pos, len, b, i;
            cin >> pos >> len >> b >> i;
            editor.format(pos, len, b, i);
        }
        else if (cmd == "UNDO") {
            editor.undo();
        }
        else if (cmd == "REDO") {
            editor.redo();
        }
        else if (cmd == "PRINT") {
            editor.print();
        }
    }
    return 0;
}