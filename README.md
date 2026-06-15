# meta-llm

## 1. Overview
```
Get up and running with large language models locally. 

This layer integrates ollama and llama.cpp into Yocto/OE platform

- Ollama: The easiest way to build with open models [1]

- Llama.cpp: Run LLM Inference in C/C++ [2]

[1] https://ollama.com/
[2] https://llama-cpp.com/
```

## 2. Comparison between Ollama and llama.cpp 
```
The comparison between Ollama and llama.cpp is primarily a choice between
convenience and control. While Ollama is built on top of llama.cpp, they
serve very different user needs. 
```

### 2.1 Core Differences
```
- Ollama (The "App Store" Experience): Designed as a user-friendly wrapper. It packages
models into a "Modelfile" format (similar to Docker), handles automatic GPU/CPU
offloading, and manages the lifecycle of the model server. It is ideal for users who
want to run AI models with a single command like `ollama run llama3`

- llama.cpp (The "Engine"): The underlying C++ library that makes local inference
possible. It is highly optimized for performance and portability. It requires more
manual configuration (e.g., specifying thread counts, GPU layers, and managing
.gguf files manually), but offers maximum efficiency. 
```

### 2.2 Performance and Efficiency
```
- Speed: Benchmarks often show that llama.cpp can be significantly faster (sometimes up
to 80% higher token throughput) because it allows for granular hardware optimization
that Ollama abstracts away.

- Resource Management: Ollama is a persistent background service, which can consume
RAM even when not in use unless configured otherwise. llama.cpp is typically run as a
standalone binary that exits completely when stopped.

- Parallelism: llama.cpp excels at handling concurrent requests. While Ollama has improved,
llama.cpp's server mode is generally more stable for multi-user or agentic workloads. 
```

### 2.3 Feature Comparison
```
Feature 	            Ollama	                       llama.cpp
----------------------------------------------------------------------------------------
Ease of Use	Extremely high (One-click installers)    Moderate (Requires CLI knowledge)
----------------------------------------------------------------------------------------
Model Management	    Built-in Ollama Library        Manual download of GGUF files
----------------------------------------------------------------------------------------
Customization	        Limited (Modelfiles)           Full (Command-line flags
                                                       or every parameter)
----------------------------------------------------------------------------------------
API                     OpenAI-compatible &            OpenAI-compatible server
                        Native REST
----------------------------------------------------------------------------------------
Hardware                Auto-detects                   Requires manual compilation
                        (Mac, Linux, Windows)          or specific binaries
```

### 2.4. Which Should You Use?
```
- Choose Ollama if: You are a developer who wants an easy API to build apps, a student
who wants to chat with models quickly, or a casual user who doesn't want to fiddle
with command-line parameters. It is the best starting point for local LLMs.

- Choose llama.cpp if: You need absolute maximum performance, want to run the latest
experimental models before they hit the Ollama library, or are deploying AI on
constrained hardware (like edge devices) where every megabyte of RAM counts.
```

## 3. Project License
```
Copyright (C) 2026 Wind River Systems, Inc.

All metadata is MIT licensed unless otherwise stated. Source code included
in tree for individual recipes is under the LICENSE stated in each recipe
(.bb file) unless otherwise stated.
```

## 4. Legal Notices
```
If product is based on Wind River Linux:

All product names, logos, and brands are property of their respective owners.
All company, product and service names used in this software are for identification
purposes only. Wind River is a registered trademark of Wind River Systems.

Disclaimer of Warranty / No Support: Wind River does not provide support and
maintenance services for this software, under Wind River’s standard Software
Support and Maintenance Agreement or otherwise. Unless required by applicable
law, Wind River provides the software (and each contributor provides its
contribution) on an “AS IS” BASIS, WITHOUT WARRANTIES OF ANY KIND, either express
or implied, including, without limitation, any warranties of TITLE, NONINFRINGEMENT,
MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE. You are solely responsible
for determining the appropriateness of using or redistributing the software
and assume any risks associated with your exercise of permissions under the license.
```

## 5. Build and Run
```
See README-ollama.md for ollama details
See README-llama-cpp.md for llama.cpp details
```
