<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>URL Shortener</title>
    <style>
        table {
            border-collapse: collapse;
            width: 80%;
            margin: 20px auto;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        button {
            margin: 0 5px;
        }
    </style>
</head>
<body>
<h3 style="text-align: center;">Shortened URL Details</h3>
<table>
    <tr>
        <th>Original URL</th>
        <th>Shortened URL</th>
        <th>Actions</th>
    </tr>
    <tr>
        <td style="word-break: break-all;">${originalUrl}</td>
        <td><a href="/tiny/${tinyUrl}">/tiny/${tinyUrl}</a></td>
        <td>
            <button onclick="copyToClipboard('/tiny/${tinyUrl}')">Copy URL</button>
            <button onclick="generateQRCode('/tiny/${tinyUrl}')">Generate QR Code</button>
        </td>
    </tr>
</table>
<script>
    function copyToClipboard(url) {
        navigator.clipboard.writeText(window.location.origin + url);
        alert('URL copied to clipboard!');
    }

    function generateQRCode(url) {
        const qrWindow = window.open('', '_blank', 'width=400,height=400');
        qrWindow.document.write('<html><head><title>QR Code</title></head><body>');
        qrWindow.document.write('<h3>QR Code for URL</h3>');
        qrWindow.document.write('<img src="https://api.qrserver.com/v1/create-qr-code/?data=' + encodeURIComponent(window.location.origin + url) + '&size=200x200" />');
        qrWindow.document.write('</body></html>');
    }
</script>
</body>
</html>
