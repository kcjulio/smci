CREATE DATABASE dbsdai;

USE dbsdai;

CREATE TABLE clientes
(
	idcliente	INT PRIMARY KEY AUTO_INCREMENT,
    nome	VARCHAR(100) NOT NULL,
    data_nascimento		VARCHAR(10),
    cpf		VARCHAR(15) NOT NULL,
    email	VARCHAR(50),
    data_cadastro	TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuarios
(
	idusuario	INT PRIMARY KEY,
    nome	VARCHAR(100) NOT NULL,
    data_nascimento		VARCHAR(10) NOT NULL,
    rg		VARCHAR(15) NOT NULL,
    cpf		VARCHAR(15) NOT NULL,
    endereco	VARCHAR(150) NOT NULL,
    fone	VARCHAR(15) NOT NULL,
    email	VARCHAR(50),
    data_admissao	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    login	VARCHAR(30) NOT NULL,
    senha	VARCHAR(30) NOT NULL,
    perfil	VARCHAR(5) NOT NULL
);

CREATE TABLE bombeiros
(
	idbombeiros		INT PRIMARY KEY AUTO_INCREMENT,
    nome	VARCHAR(100) NOT NULL,
    fone	VARCHAR(15) NOT NULL,
    email	VARCHAR(50),
    data_cadastro	TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE endereco
(
    cidade		VARCHAR(50) NOT NULL,
    uf		VARCHAR(5) NOT NULL,
	bairro	VARCHAR(50) NOT NULL,
    rua		VARCHAR(50) NOT NULL,
    numero	INT NOT NULL,
    apto	INT,
    complemento		VARCHAR(100),
    idcliente	INT PRIMARY KEY,
    
    FOREIGN KEY (idcliente) REFERENCES clientes (idcliente)
);

CREATE TABLE endereco_bombeiros
(
	idendereco		INT PRIMARY KEY AUTO_INCREMENT,
    cidade		VARCHAR(50) NOT NULL,
    uf		VARCHAR(5) NOT NULL,
	bairro	VARCHAR(50) NOT NULL,
    rua		VARCHAR(50) NOT NULL,
    numero	INT NOT NULL,
    apto	INT,
    complemento		VARCHAR(100),
    idbombeiros		INT NOT NULL,
    
    FOREIGN KEY (idbombeiros) REFERENCES bombeiros (idbombeiros)
);

CREATE TABLE telefones
(
	idfone		INT PRIMARY KEY AUTO_INCREMENT,
    fone	VARCHAR(15) NOT NULL,
    tipo	VARCHAR(15) NOT NULL,
    idcliente	INT NOT NULL,
    
    FOREIGN KEY (idcliente) REFERENCES clientes (idcliente)
);

CREATE TABLE arduino
(
	idarduino	INT PRIMARY KEY AUTO_INCREMENT,
    localizacao		VARCHAR(100),
    idcliente	INT NOT NULL,
    
    FOREIGN KEY (idcliente) REFERENCES clientes (idcliente)
);

CREATE TABLE sensores
(
    idsensores	INT PRIMARY KEY AUTO_INCREMENT,
    data_hora	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    gps		VARCHAR(30) NOT NULL,
    temperatura		FLOAT NOT NULL,
    humidade	FLOAT NOT NULL,
    gases		FLOAT NOT NULL,
    chamas		FLOAT NOT NULL,
    tensao_bateria		FLOAT NOT NULL,
    intensidade		INT NOT NULL,
    idarduino	INT NOT NULL,
    idcliente	INT NOT NULL,
    
    FOREIGN KEY (idarduino) REFERENCES arduino (idarduino),
    FOREIGN KEY (idcliente) REFERENCES clientes (idcliente)
);

CREATE TABLE historico
(
	idhistorico		INT PRIMARY KEY AUTO_INCREMENT,
    data_hora	VARCHAR(30) NOT NULL,
    gps		VARCHAR(30) NOT NULL,
    temperatura		FLOAT NOT NULL,
    humidade	FLOAT NOT NULL,
    gases		FLOAT NOT NULL,
    chamas		FLOAT NOT NULL,
    tensao_bateria		FLOAT NOT NULL,
    intensidade		INT NOT NULL,
    idarduino	INT NOT NULL,
    idcliente	INT NOT NULL,
    
    FOREIGN KEY (idcliente) REFERENCES clientes (idcliente)
);

delimiter //
CREATE TRIGGER calcula_intensidade BEFORE UPDATE ON sensores
FOR EACH ROW
BEGIN
    
	set @temperature = NEW.temperatura;
    set @humidity = NEW.humidade;
    set @gas = NEW.gases;
    set @flame = NEW.chamas;
    set @voltage = NEW.tensao_bateria;
    
    set @warning = 0;
    
    if (@voltage < 4.8) then
		set @warning = @warning + 1;
		set NEW.intensidade = @warning;
	end if;
    
    if (@humidity < 25.0) then
		set @warning = @warning + 2;
		set NEW.intensidade = @warning;
	end if;
    
    if (@temperature > 45.0) then
		set @warning = @warning + 4;
		set NEW.intensidade = @warning;
	end if;
    
    if (@gas > 0.4) then
		set @warning = @warning + 8;
		set NEW.intensidade = @warning;
	end if;
    
    if (@flame > 0.4) then
		set @warning = @warning + 16;
		set NEW.intensidade = @warning;
	end if;
    
    set NEW.intensidade = @warning;
END //

delimiter //
CREATE TRIGGER prepara_sensores AFTER INSERT ON arduino
FOR EACH ROW
BEGIN
	INSERT INTO sensores (gps, temperatura, humidade, gases, chamas, tensao_bateria, intensidade, idarduino, idcliente) VALUES (' ', 0, 0, 0, 0, 5, 0, NEW.idarduino, NEW.idcliente);
END //

-- DROP TRIGGER calcula_intensidade;
-- DROP DATABASE dbsdai;