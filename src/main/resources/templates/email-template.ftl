<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>${subject}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }
        .container {
            width: 90%;
            max-width: 600px;
            margin: 20px auto;
            background-color: #ffffff;
            border: 1px solid #dddddd;
            border-radius: 8px;
            overflow: hidden;
        }
        .header {
            background-color: #004a99; /* Azul escuro do "Agiota Bank" */
            color: #ffffff;
            padding: 20px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 24px;
        }
        .content {
            padding: 30px;
            color: #333333;
            line-height: 1.6;
        }
        /* Esta é a parte importante: 
         Ela preserva as quebras de linha (\n) da sua mensagem original.
        */
        .content pre {
            font-family: Arial, sans-serif;
            white-space: pre-wrap; /* Mantém as quebras de linha */
            word-wrap: break-word;
            margin: 0;
        }
        .footer {
            background-color: #f9f9f9;
            color: #777777;
            padding: 20px;
            text-align: center;
            font-size: 12px;
            border-top: 1px solid #eeeeee;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Agiota Bank</h1>
        </div>
        <div class="content">
            <pre>${messageBody}</pre>
        </div>
        <div class="footer">
            <p>&copy; ${.now?string("yyyy")} Agiota Bank. Todos os direitos reservados.</p>
            <p>Este é um e-mail automático, por favor, não responda.</p>
        </div>
    </div>
</body>
</html>