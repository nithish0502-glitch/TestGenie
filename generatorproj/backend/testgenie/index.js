const express = require("express");
const fs = require("fs-extra");
const path = require("path");
const cors = require("cors");
const archiver = require("archiver");
const axios = require("axios");
const unzipper = require("unzipper")
require("dotenv").config(); // Load environment variables
const AdmZip = require("adm-zip");

// Initialize Express app
const app = express();
app.use(cors());
app.use(express.json());

let lastGeneratedModels = [];

// app.post("/generate-suggestion", async (req, res) => {
//   try {
//     const { relationshipType, modelCount, regenerate } = req.body;

//     if (!relationshipType || !modelCount || modelCount < 2) {
//       return res.status(400).json({ error: "Invalid input. Provide relationshipType and at least 2 models." });
//     }

//     // Single AI request for entity models, exceptions, and JPQL queries
//     const prompt = `
//   You are an AI that generates **unique entity models**, **realistic custom exceptions**, and **JPQL queries** for a Spring Boot project.

//   ### 1️⃣ **Generate 10 unique entity pairs** related by **${relationshipType}**.
//      - Each pair should be **real-world use cases** and **follow Java naming conventions**.
//      - **If One-to-One:** The first entity should uniquely relate to only one instance of the second entity.
//      - **If One-to-Many:** The first entity should relate to multiple instances of the second entity.
//      - Format: "Entity1 - Entity2".
//      ${regenerate ? `- Ensure these new pairs are different from previous: ${lastGeneratedModels.join(", ")}` : ""}

//   ### 2️⃣ **For each entity pair, generate 3 meaningful custom exceptions based on the relationship type.**
//      - **One-to-One Relationship:**  
//        - Handle **logical constraints** preventing multiple assignments.
//        - Ensure **entities meet required conditions** before assignment.
//        - Examples:
//          - **Student - Classroom**  
//            - \`StudentAlreadyEnrolledException\` - "Students can only be enrolled in one classroom per term to avoid scheduling conflicts."
//            - \`ClassroomOverCapacityException\` - "Classrooms have a fixed capacity for safety and quality of education."
//          - **Driver - Vehicle**  
//            - \`DriverAlreadyAssignedException\` - "Drivers can only operate one vehicle at a time for safety and legal reasons."
//            - \`VehicleUnderMaintenanceException\` - "Vehicles must be in working condition before being assigned to drivers."
//      - **One-to-Many Relationship:**  
//        - Ensure **data integrity** when handling multiple relationships.
//        - Consider **business rules for managing multiple assignments**.
//        - Examples:
//          - **Library - Books**  
//            - \`LibraryBookLimitExceededException\` - "Libraries have a finite amount of physical space for storing books."
//            - \`BookAlreadyRegisteredInLibraryException\` - "Each book should have a unique registration in a library to avoid duplication."
//          - **Hospital - Patients**  
//            - \`PatientUnderQuarantineException\` - "Patients under quarantine must remain isolated to prevent the spread of infectious diseases."
//            - \`PatientTransferInProgressException\` - "Patients in the process of being transferred cannot be admitted, discharged, or assigned to new treatments."
//      - **Format:** "**ExceptionName - Meaningful Description**"

//      ### 3️⃣ **For each first entity in the pair, generate 3 complex JPQL query descriptions.**
//      - Each query should involve **filtering, aggregation, or joining** with the second entity.
//      - Ensure **each query is different**, covering various real-world use cases Note: Dont use date formation.
//      - Examples:
//        - **For Student - Classroom:**
//          - "Retrieve all students younger than 18 years old."
//          - "List classrooms that have more than 30 students enrolled."
//          - "Find students whose names start with a specified prefix."
//        - **For Library - Books:**
//          - "Find all libraries located in a specified region."
//          - "Find libraries with fewer than 100 available books."
//          - "List books available in a given location."
//      - **Format:**  
//        - Query 1: <Query Description>  
//        - Query 2: <Query Description>  
//        - Query 3: <Query Description>  
  
//   **Response format:**
//   ### Response format (STRICTLY FOLLOW THIS):
//   Entity1 - Entity2  
//   - **Exception1** - Description  
//   - **Exception2** - Description  
//   - **Exception3** - Description  
//   **JPQL Queries:**  
//   Query 1: Query Description  
//   Query 2: Query Description  
//   Query 3: Query Description  
  
// `;

//     const aiResponse = await sendToAI([{ role: "system", content: prompt }]);
//     if (!aiResponse) return res.status(500).json({ error: "AI generation failed" });

//     // Processing AI Response
//     const lines = aiResponse.split("\n").map((line) => line.trim()).filter((line) => line);
//     const maxSuggestions = 10; // Limit to 10 suggestions
// const suggestions = [];
// let currentPair = null;
// let exceptions = [];
// let jpqlQueries = [];


// for (const line of lines) {
//   if (suggestions.length >= maxSuggestions) break; // Stop processing after 10

//   if (line.includes(" - ") && !line.startsWith("-")) {
//     if (currentPair) {
//       suggestions.push({ model: currentPair, exceptions, jpqlQueries });
//     }
//     currentPair = line;
//     exceptions = [];
//     jpqlQueries =[];
//   } else if (line.startsWith("-")) {
//     exceptions.push(line.replace("- ", ""));
//   } else if (line.startsWith("Query")) {
//     jpqlQueries.push(line.replace(/^Query \d+: /, "").trim()); // Capture multiple JPQL queries
// }
// }

// if (currentPair && suggestions.length < maxSuggestions) {
//   suggestions.push({ model: currentPair, exceptions, jpqlQueries });
// }

//     lastGeneratedModels = suggestions.map((s) => s.model);

//     res.json({
//       message: "✅ Suggestions generated successfully!",
//       suggestions,
//     });
//   } catch (error) {
//     res.status(500).json({ error: "Suggestion generation failed: " + error.message });
//   }
// });

app.post("/generate-suggestion", async (req, res) => {
  try {
    const { relationshipType, modelCount, regenerate } = req.body;

    if (!relationshipType || !modelCount || modelCount < 2) {
      return res.status(400).json({ error: "Invalid input. Provide relationshipType and at least 2 models." });
    }

    // Single AI request for entity models, exceptions, and JPQL queries
    const prompt = `
You are an AI that generates **strictly formatted** and **complete** entity models, exceptions, and JPQL queries for a Spring Boot project. 

🚨 **IMPORTANT INSTRUCTIONS:**
- **DO NOT** leave any field empty.
- **EVERY entity pair MUST have exactly 3 exceptions and 3 JPQL queries.**
- **STRICTLY follow the response format below** (No extra text, no missing fields).

---

### 1️⃣ **Generate 10 unique entity pairs** related by **${relationshipType}**.
- Each pair should be a **real-world example** and **follow Java naming conventions**.
- **If One-to-One:** The first entity uniquely relates to one instance of the second entity.
- **If One-to-Many:** The first entity relates to multiple instances of the second entity.
- **Each entity must have at least 5 attributes with meaningful names and types relevant to its domain.**
- **Format:** "Entity1 - Entity2"
${regenerate ? `- Ensure these are different from previous: ${lastGeneratedModels.join(", ")}` : ""}

---
🔹 **You are NOT limited to the sample examples below.** You should create more **real-world examples**, but the **complexity level must not exceed the provided samples.**  
🔹 The difficulty level is set based on these examples and should remain the same.

### 2️⃣ **For each entity pair, generate 3 meaningful custom exceptions.**
- Each exception must be **real-world relevant**.
- Examples for one to one:
          - **Student - Classroom** 
            - \`StudentAlreadyEnrolledException\` - "Students can only be enrolled in one classroom per term to avoid scheduling conflicts."
            - \`ClassroomOverCapacityException\` - "Classrooms have a fixed capacity for safety and quality of education."
          - **Driver - Vehicle**  
            - \`DriverAlreadyAssignedException\` - "Drivers can only operate one vehicle at a time for safety and legal reasons."
            - \`VehicleUnderMaintenanceException\` - "Vehicles must be in working condition before being assigned to drivers."
- Examples:
          - **Library - Books**  
            - \`LibraryBookLimitExceededException\` - "Libraries have a finite amount of physical space for storing books."
            - \`BookAlreadyRegisteredInLibraryException\` - "Each book should have a unique registration in a library to avoid duplication."
          - **Hospital - Patients**  
            - \`PatientUnderQuarantineException\` - "Patients under quarantine must remain isolated to prevent the spread of infectious diseases."
            - \`PatientTransferInProgressException\` - "Patients in the process of being transferred cannot be admitted, discharged, or assigned to new treatments."
- **Format:** "**ExceptionName - Meaningful Description**"

---

### 3️⃣ **For each first entity, generate exactly 3 JPQL queries.**
- **Each query must be unique.**
Note: Dont use date formation.
- Examples:
        - **For Student - Classroom:**
          - "Retrieve all students younger than 18 years old."
          - "List classrooms that have more than 30 students enrolled."
          - "Find students whose names start with a specified prefix."
        - **For Library - Books:**
          - "Find all libraries located in a specified region."
          - "Find libraries with fewer than 100 available books."
          - "List books available in a given location."
- **Do NOT generate less or more than 3 queries.**
- **Format:**  
  - <Query Description>  
  - <Query Description>  
  - <Query Description>  

---

### **STRICT RESPONSE FORMAT:**
\`\`\`
Entity1 - Entity2  
- **Exception1** - Description  
- **Exception2** - Description  
- **Exception3** - Description  
**JPQL Queries:**  
Query 1: Query Description  
Query 2: Query Description  
Query 3: Query Description  
\`\`\`
🔴 **NO missing fields! If AI fails to provide any required field, regenerate with complete data.**
`;


    const aiResponse = await sendToAI([{ role: "system", content: prompt }]);
    if (!aiResponse) return res.status(500).json({ error: "AI generation failed" });

    // Processing AI Response
    const lines = aiResponse.split("\n").map((line) => line.trim()).filter((line) => line);
    const maxSuggestions = 10;
    const suggestions = [];
    let currentPair = null;
    let exceptions = [];
    let jpqlQueries = [];

    for (const line of lines) {
      if (suggestions.length >= maxSuggestions) break;

      if (line.includes(" - ") && !line.startsWith("-") && !line.startsWith("Query")) {
        // If we encounter a new entity pair, save the previous one first
        if (currentPair) {
          if (exceptions.length === 0 || jpqlQueries.length === 0) {
            throw new Error(`Missing data for ${currentPair}. Exceptions or JPQL queries are empty.`);
          }
          suggestions.push({ model: currentPair, exceptions, jpqlQueries });
        }
        currentPair = line;
        exceptions = [];
        jpqlQueries = [];
      } else if (line.startsWith("- **")) {
        // Extract exceptions correctly, handling markdown
        const match = line.match(/-\s\*\*(.*?)\*\*\s-\s(.*)/);
        if (match) {
          exceptions.push(`${match[1]} - ${match[2]}`);
        }
      } else if (line.startsWith("Query")) {
        // Extract JPQL Queries
        const queryMatch = line.match(/^Query \d+:\s(.*)/);
        if (queryMatch) {
          jpqlQueries.push(queryMatch[1]);
        }
      }
    }

    // Push the last entity pair if it exists
    if (currentPair) {
      if (exceptions.length === 0 || jpqlQueries.length === 0) {
        throw new Error(`Missing data for ${currentPair}. Exceptions or JPQL queries are empty.`);
      }
      if (suggestions.length < maxSuggestions) {
        suggestions.push({ model: currentPair, exceptions, jpqlQueries });
      }
    }

    // **Final Validation for Empty Arrays**
    for (const suggestion of suggestions) {
      if (!suggestion.model) {
        throw new Error(`Invalid suggestion format: Model is missing.`);
      }
      if (suggestion.exceptions.length === 0) {
        throw new Error(`Invalid suggestion format: Exceptions array is empty for model ${suggestion.model}.`);
      }
      if (suggestion.jpqlQueries.length === 0) {
        throw new Error(`Invalid suggestion format: JPQL Queries array is empty for model ${suggestion.model}.`);
      }
    }

    lastGeneratedModels = suggestions.map((s) => s.model);

    res.json({
      message: "✅ Suggestions generated successfully!",
      suggestions,
    });
  } catch (error) {
    res.status(500).json({ error: "Suggestion generation failed: " + error.message });
  }
});


async function sendToAI(messages) {
  console.log("Requested");
  try {
    const response = await axiosInstance.post(
      "https://api.mistral.ai/v1/chat/completions",
      {
        model: "mistral-small-latest",
        temperature: 1.5,
        top_p: 1,
        max_tokens: 8000,
        messages: messages,
        response_format: { type: "text" },
      },
      {
        headers: {
          Authorization: `Bearer ${process.env.MISTRAL_API_KEY}`,
          "Content-Type": "application/json",
        },
        timeout: 60000, // ⏳ Set a timeout
      }
    );

    return response.data.choices[0].message.content;
  } catch (error) {
    console.error("❌ AI Request Failed:", error.response?.data || error.message);
    return null;
  }
}


const axiosInstance = axios.create({
  timeout: 60000, // 60 seconds timeout
});

const findFile = async (dir, fileName) => {
  console.log(`Searching in directory: ${dir}`);
  const files = await fs.readdir(dir, { withFileTypes: true });
  console.log("Files found:", files);

  for (const file of files) {
    console.log("Checking file:", file.name);
    const filePath = path.join(dir, file.name);

    if (file.isDirectory()) {
      const result = await findFile(filePath, fileName); // Recursively search subdirectories
      if (result) return result;
    } else if (file.name === fileName && file.name.endsWith(".java")) {
      console.log(`Found file: ${filePath}`);
      return filePath; // Return the full path if the file is found
    }
  }

  return null; // Return null if the file is not found
};

app.post("/modify-test-file", async (req, res) => {
  try {
    const { selectedModel, relationshipType, selectedException, selectedQuery } = req.body;

    if (!selectedModel || !relationshipType || !selectedException || !selectedQuery) {
      return res.status(400).json({ error: "Please provide selectedModel, relationshipType, selectedException, and selectedQuery." });
    }

    // Extract entity names
    const [entity1, entity2] = selectedModel.split(" - ");

    // Define test file paths
    const testFilePathMap = {
      OneToOne: path.join(__dirname, "sample_test_files/sample_onetoone/SpringappApplicationTests.java"),
      OneToMany: path.join(__dirname, "sample_test_files/sample_onetomany/SpringappApplicationTests.java"),
    };

    const testFilePath = testFilePathMap[relationshipType];

    if (!testFilePath || !(await fs.pathExists(testFilePath))) {
      return res.status(404).json({ error: `Test file for ${relationshipType} not found` });
    }
    let relationshipPrompt = "";
    if (relationshipType === "OneToOne") {
      relationshipPrompt = `
      🚨 **Important Rules (For One-to-One Relationships Only):**  
      1️⃣ **DO NOT copy existing test names** like \`testCreatePatient_MissingFields\` or \`testGetMedicalRecordsByDoctor_Success\`.  
         - These belong to a different use case and should not be reused.  
      2️⃣ **Generate a completely new test case** for the exception **"${selectedException}"** (Use a fresh, meaningful test name).  
      3️⃣ **Generate a completely new test case** to validate the JPQL query **"${selectedQuery}"** (Use a fresh test name).  
      4️⃣ Ensure the test logic is relevant to **${entity1} and ${entity2}** while keeping structure intact.  
      `;
    } else if (relationshipType === "OneToMany") {
      relationshipPrompt = `
      🚨 **Important Rules (For One-to-Many Relationships Only):**  
      1️⃣ **DO NOT copy existing test names** like \`testDuplicateAddEvent\` or \`testGetTicketsByEventDateAndLocation\`.  
      2️⃣ **Generate a completely new test case** for the exception **"${selectedException}"** (Use a fresh, meaningful test name).  
      3️⃣ **Generate a completely new test case** to validate the JPQL query **"${selectedQuery}"** (Use a fresh test name).  
      4️⃣ Ensure the test logic is relevant to **${entity1} as the parent and ${entity2} as the child** while keeping structure intact.    
      `;
    }
    // Read the existing test file
    let content = await fs.readFile(testFilePath, "utf8");

    // AI Prompt - Modify test logic based on model, exception, and query
    const messages = [
      {
        role: "system",
        content: `You are an AI that modifies a Spring Boot test file. Follow these rules:
1️⃣ **DO NOT change the class name, structure, or test framework imports**.
2️⃣ Replace existing test logic with **new logic relevant to the model**: ${entity1} and ${entity2}.
3️⃣ Implement test logic for the following **custom exception**: ${selectedException}, ensuring that the related attributes exist in the entity.
4️⃣ Implement a test case to validate the following **JPQL query**: "${selectedQuery}".
5️⃣ **DO NOT add test cases for attributes or functionalities that do not exist in the model**.
6️⃣ **Ensure that all test cases align with the entity structure, including attributes, relationships, and validations**.
7️⃣ **DO NOT remove or reduce the number of test cases. The modified file must retain the same number of tests.**
8️⃣ **Maintain proper structure and follow Spring Boot testing best practices**.
9️⃣ Return **ONLY** the modified Java test file.
🔟 **Strictly follow the exact API format as in the sample test file.**
        `,
      },
      {
        role: "user",
        content: `Here is my existing test file. Modify it to replace the entity references with **${entity1} and ${entity2}**, ensuring meaningful test logic while keeping the structure the same.  
        ${relationshipPrompt}
        🔄 **Existing Sample Test File (Modify & Enhance, Not Copy!):**  
        \n\n${content.substring(0, 19000)}`,
      },
    ];

    // Send to AI
    const aiResponse = await sendToAI(messages);
    if (!aiResponse) {
      return res.status(500).json({ error: "AI modification failed" });
    }

    // Extract the existing zip file
    const zipFilePath = path.join(__dirname, "./samplescaff/springappscaff.zip");
    const extractPath = path.join(__dirname, "extracted");
    await fs.ensureDir(extractPath);
    await fs.emptyDir(extractPath);

    const zip = new AdmZip(zipFilePath);
zip.extractAllTo(extractPath, true); // `true` ensures overwriting existing files
    
    const testFileInExtracted = path.join(__dirname, "./extracted/springappscaff/junit/test/java/com/examly/springapp/SpringappApplicationTests.java");
    console.log(extractPath);
    console.log(testFileInExtracted);
    // Remove first and last lines from the AI response before saving
    let aiResponseLines = aiResponse.split("\n");
    if (aiResponseLines.length > 2) {
      aiResponseLines = aiResponseLines.slice(1, -1); // Remove first and last lines
    }
    const cleanedAiResponse = aiResponseLines.join("\n");

    // Replace the content of the test file with the modified content
    await fs.writeFile(testFileInExtracted, cleanedAiResponse, "utf8");

    const originalFolder = path.join(extractPath, "springappscaff");
    const renamedFolder = path.join(extractPath, "springapptests");
    await fs.rename(originalFolder, renamedFolder);

    // Create a new zip file
    const updatedZipPath = path.join(__dirname, "springapptests.zip");
    const output = fs.createWriteStream(updatedZipPath);
    const archive = archiver("zip", { zlib: { level: 9 } });

    output.on("close", async () => {
        res.download(updatedZipPath, "springapptests.zip", async (err) => {
            if (err) console.error("Download failed:", err);

            // Cleanup
            await fs.remove(extractPath);
            await fs.remove(updatedZipPath);
        });
    });

    archive.on("error", (err) => {
        throw err;
    });

    archive.pipe(output);
    archive.directory(renamedFolder, "springapptests"); 
    await archive.finalize();
  } catch (error) {
    res.status(500).json({ error: "Modification failed: " + error.message });
  }
});

 
const PORT = 5000;
app.listen(PORT, () => {
  console.log(`🚀 Server running on port ${PORT}`);
});
