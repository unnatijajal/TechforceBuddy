from transformers import pipeline
from flask import Flask, request, jsonify

app = Flask(__name__)

# Load GPT-2 model and tokenizer
summarizer = pipeline("summarization", model="facebook/bart-large-cnn")

@app.route('/generate', methods=['POST'])
def generate_text():
    data = request.json
    input_text = data.get('input_text', '')

    return jsonify({'generated_text': summarizer(input_text, max_length=300, min_length=30, do_sample=False)})

if __name__ == '__main__':
    app.run('192.168.1.214',port=5000)